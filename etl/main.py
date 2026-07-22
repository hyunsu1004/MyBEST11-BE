from dotenv import load_dotenv
from config import COMPETITIONS
from load import (
    get_connection, upsert_league, upsert_team,
    upsert_season, upsert_player, upsert_player_season_stat,
    load_players_from_csv, create_run_log_table, log_run_start, log_run_finish,
)
from logger_config import get_logger
from validation import ValidationRunner  # <- 추가
import csv

load_dotenv()

logger = get_logger("etl.main")

CURRENT_SEASON = "2025-26"
TEAMS_CSV_PATH = "teams.csv"
PLAYERS_CSV_PATH = "players.csv"


def load_teams_from_csv(filepath: str) -> list[str]:
    with open(filepath, encoding="utf-8") as f:
        reader = csv.DictReader(f)
        return [row["team_name"] for row in reader]


def run():
    conn = get_connection()
    create_run_log_table(conn)
    run_id = log_run_start(conn, "mysql_load")  # 이 run_id를 검증 로그에도 그대로 재사용

    rows_processed = 0

    try:
        for code, league_name in COMPETITIONS.items():
            logger.info(f"[{league_name}] : 리그처리 시작")

            league_id = upsert_league(conn, league_name, "England")
            season_id = upsert_season(conn, CURRENT_SEASON)

            team_names = load_teams_from_csv(TEAMS_CSV_PATH)

            for team_name in team_names:
                team_id = upsert_team(conn, team_name, league_id)
                logger.info(f" -{team_name} 처리 중 ...")

                players = load_players_from_csv(PLAYERS_CSV_PATH, team_name)
                if not players:
                    logger.warning(f"  (CSV에 등록된 선수 없음 - 건너뜀)")
                    continue

                for player in players:
                    player_id = upsert_player(
                        conn, player["name"], player["nationality"], player["primary_position"]
                    )
                    upsert_player_season_stat(conn, player_id, team_id, season_id)
                    rows_processed += 1
                logger.info(f"   선수 {len(players)} 명 적재 완료")

        # ================= 여기부터 검증 단계 추가 =================
        validator = ValidationRunner(conn, run_id=str(run_id), step="MYSQL_LOAD")
        validator.check_row_count("player", min_rows=1)
        validator.check_row_count("team", min_rows=1)
        validator.check_null_ratio("player", column="name", max_ratio=0.0)
        validator.check_null_ratio("player", column="primary_position", max_ratio=0.0)
        validator.check_duplicates("player", columns=["name", "nationality"])
        validator.check_schema("player", required_columns=["id", "name", "nationality", "primary_position"])
        validator.check_schema("team", required_columns=["id", "name", "league_id"])
        result = validator.finish()
        # ==========================================================

        status = "FAILED" if result["has_failure"] else "SUCCESS"
        log_run_finish(conn, run_id, status, rows_processed)
        logger.info(f"ETL 완료 (처리 row : {rows_processed}, 검증 결과: {status})")

    except Exception as e:
        log_run_finish(conn, run_id, "FAILED", rows_processed, str(e))
        logger.error(f"ETL 실패 : {e}")
        raise
    finally:
        conn.close()


if __name__ == "__main__":
    run()