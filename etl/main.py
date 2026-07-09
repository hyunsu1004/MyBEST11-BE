import csv
from config import COMPETITIONS
from load import (
    get_connection, upsert_league, upsert_team,
    upsert_season, upsert_player, upsert_player_season_stat,
    load_players_from_csv,
)

CURRENT_SEASON = "2025-26"
TEAMS_CSV_PATH = "teams.csv"
PLAYERS_CSV_PATH = "players.csv"


def load_teams_from_csv(filepath: str) -> list[str]:
    with open(filepath, encoding="utf-8") as f:
        reader = csv.DictReader(f)
        return [row["team_name"] for row in reader]


def run():
    conn = get_connection()

    for code, league_name in COMPETITIONS.items():
        print(f"[{league_name}] 리그 처리 시작")
        league_id = upsert_league(conn, league_name, "England")
        season_id = upsert_season(conn, CURRENT_SEASON)

        team_names = load_teams_from_csv(TEAMS_CSV_PATH)

        for team_name in team_names:
            team_id = upsert_team(conn, team_name, league_id)
            print(f"  - {team_name} 처리 중...")

            players = load_players_from_csv(PLAYERS_CSV_PATH, team_name)
            if not players:
                print(f"    (CSV에 등록된 선수 없음 - 건너뜀)")
                continue

            for player in players:
                player_id = upsert_player(
                    conn, player["name"], player["nationality"], player["primary_position"]
                )
                upsert_player_season_stat(conn, player_id, team_id, season_id)
            print(f"    선수 {len(players)}명 적재 완료")

    conn.close()
    print("ETL 완료")


if __name__ == "__main__":
    run()