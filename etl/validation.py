import uuid
import datetime
from typing import List, Optional

from notify_slack import send_validation_alert


class ValidationRunner:
    def __init__(self, mysql_conn, run_id: Optional[str] = None, step: str = "MYSQL_LOAD"):
        self.conn = mysql_conn
        self.run_id = run_id or str(uuid.uuid4())
        self.step = step
        self.results = []

    def check_row_count(self, table: str, min_rows: int = 1):
        cursor = self.conn.cursor()
        cursor.execute(f"SELECT COUNT(*) FROM {table}")
        count = cursor.fetchone()[0]
        if count < min_rows:
            self._record("ROW_COUNT", "FAIL",
                         f"{table} 테이블 row 수가 {count}건으로 최소 기준({min_rows}) 미달")
        else:
            self._record("ROW_COUNT", "PASS", f"{table} row 수: {count}건")

    def check_null_ratio(self, table: str, column: str, max_ratio: float = 0.0):
        cursor = self.conn.cursor()
        cursor.execute(f"SELECT COUNT(*), SUM(CASE WHEN {column} IS NULL THEN 1 ELSE 0 END) FROM {table}")
        total, null_count = cursor.fetchone()
        null_count = null_count or 0
        if total == 0:
            self._record("NULL_RATIO", "WARN", f"{table} 테이블이 비어있어 null 비율 계산 불가")
            return
        ratio = null_count / total
        if ratio > max_ratio:
            self._record("NULL_RATIO", "FAIL",
                         f"{table}.{column} null 비율 {ratio:.1%} (허용치 {max_ratio:.1%} 초과, {null_count}/{total}건)")
        else:
            self._record("NULL_RATIO", "PASS", f"{table}.{column} null 비율 {ratio:.1%}")

    def check_duplicates(self, table: str, columns: List[str]):
        col_str = ", ".join(columns)
        cursor = self.conn.cursor()
        cursor.execute(f"""
            SELECT {col_str}, COUNT(*) as cnt
            FROM {table}
            GROUP BY {col_str}
            HAVING cnt > 1
        """)
        dup_rows = cursor.fetchall()
        if dup_rows:
            self._record("DUPLICATE", "FAIL",
                         f"{table} 테이블에서 ({col_str}) 기준 중복 {len(dup_rows)}그룹 발견")
        else:
            self._record("DUPLICATE", "PASS", f"{table} 테이블 ({col_str}) 기준 중복 없음")

    def check_schema(self, table: str, required_columns: List[str]):
        cursor = self.conn.cursor()
        cursor.execute(f"SHOW COLUMNS FROM {table}")
        existing_columns = {row[0] for row in cursor.fetchall()}
        missing = [c for c in required_columns if c not in existing_columns]
        if missing:
            self._record("SCHEMA", "FAIL", f"{table} 테이블에 필수 컬럼 누락: {missing}")
        else:
            self._record("SCHEMA", "PASS", f"{table} 필수 컬럼 {len(required_columns)}개 모두 존재")

    def check_neo4j_consistency(self, neo4j_session, mysql_table: str, neo4j_label: str):
        cursor = self.conn.cursor()
        cursor.execute(f"SELECT COUNT(*) FROM {mysql_table}")
        mysql_count = cursor.fetchone()[0]
        result = neo4j_session.run(f"MATCH (n:{neo4j_label}) RETURN COUNT(n) as cnt")
        neo4j_count = result.single()["cnt"]
        if mysql_count != neo4j_count:
            self._record("CONSISTENCY", "FAIL",
                         f"{mysql_table}(MySQL: {mysql_count}건) vs {neo4j_label}(Neo4j: {neo4j_count}건) 불일치. "
                         f"ETL 순서(main.py -> sync_neo4j.py)가 지켜졌는지 확인 필요")
        else:
            self._record("CONSISTENCY", "PASS", f"{mysql_table}/{neo4j_label} 건수 일치 ({mysql_count}건)")

    def _record(self, check_name: str, status: str, detail: str):
        self.results.append((check_name, status, detail))
        cursor = self.conn.cursor()
        cursor.execute(
            """
            INSERT INTO validation_log (run_id, step, check_name, status, detail, checked_at)
            VALUES (%s, %s, %s, %s, %s, %s)
            """,
            (self.run_id, self.step, check_name, status, detail, datetime.datetime.now())
        )
        self.conn.commit()
        prefix = {"PASS": "✅", "WARN": "⚠️", "FAIL": "❌"}[status]
        print(f"[{prefix} {status}] {self.step}/{check_name}: {detail}")

    def finish(self, notify_on_fail: bool = True):
        fail_results = [r for r in self.results if r[1] == "FAIL"]
        if fail_results and notify_on_fail:
            send_validation_alert(
                run_id=self.run_id,
                step=self.step,
                failures=[(name, detail) for name, status, detail in fail_results]
            )
        return {
            "run_id": self.run_id,
            "step": self.step,
            "total": len(self.results),
            "fail_count": len(fail_results),
            "has_failure": len(fail_results) > 0,
        }