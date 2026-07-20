import uuid

def run_mysql_etl():
    run_id = str(uuid.uuid4())

    # ... 기존 CSV -> MySQL 적재 로직 (그대로 유지) ...

    from validation import ValidationRunner
    validator = ValidationRunner(mysql_conn, run_id=run_id, step="MYSQL_LOAD")
    validator.check_row_count("players", min_rows=1)
    validator.check_row_count("teams", min_rows=1)
    validator.check_null_ratio("players", column="name", max_ratio=0.0)
    validator.check_null_ratio("players", column="position", max_ratio=0.0)
    validator.check_duplicates("players", columns=["name", "team_id"])
    validator.check_schema("players", required_columns=["id", "name", "position", "team_id"])
    result = validator.finish()

    if result["has_failure"]:
        print(f"[중단] MySQL 적재 검증 실패. run_id={run_id} 확인 후 재실행 필요")
        return None

    return run_id


def run_neo4j_sync(run_id: str):
    # ... 기존 MySQL -> Neo4j 동기화 로직 (그대로 유지) ...

    from validation import ValidationRunner
    validator = ValidationRunner(mysql_conn, run_id=run_id, step="NEO4J_SYNC")
    validator.check_neo4j_consistency(neo4j_session, mysql_table="players", neo4j_label="Player")
    validator.check_neo4j_consistency(neo4j_session, mysql_table="teams", neo4j_label="Team")
    validator.finish()


if __name__ == "__main__":
    run_id = run_mysql_etl()
    if run_id:
        run_neo4j_sync(run_id)
    else:
        print("MySQL 단계 검증 실패로 Neo4j 동기화를 건너뜁니다.")