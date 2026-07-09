import pymysql
import csv
from config import DB_CONFIG



def get_connection():
    return pymysql.connect(**DB_CONFIG)

"""
포인트: upsert_* 함수들은 같은 스크립트를 여러 번 실행해도 중복 데이터가 안 쌓이도록
"있으면 조회, 없으면 삽입" 패턴으로 짰습니다.
ETL은 재실행이 잦은 작업이라 이 패턴이 실무에서도 기본입니다.
(참고: goals/assists/appearances는 지금 0으로 채워두는데,
실제 경기 기록 데이터는 football-data.org 무료 티어 범위를 넘어서서 이번 단계에서는 생략했습니다"""

def upsert_league(conn,name:str,country:str) ->int:
    with conn.cursor() as cursor:
        cursor.execute("SELECT id FROM league WHERE name = %s",(name,))
        row = cursor.fetchone()
        if row: #있으면 조회 없으면 삽입 구조
            return row[0]
        cursor.execute(
        "INSERT INTO league (name,country) VALUES (%s, %s)",(name,country)
        )
        conn.commit()
        return cursor.lastrowid

def upsert_team(conn,name:str,league_id:int)->int:
    with conn.cursor() as cursor:
        cursor.execute(
        "SELECT id FROM team WHERE name = %s AND league_id = %s",(name,league_id)
        )
        row = cursor.fetchone()
        if row:
            return row[0]

        cursor.execute(
        "INSERT INTO team (name,league_id) VALUES (%s, %s)",(name,league_id)
        )
        conn.commit()
        return cursor.lastrowid

def upsert_season(conn, year_range: str) -> int:
    with conn.cursor() as cursor:
        cursor.execute("SELECT id FROM season WHERE year_range = %s", (year_range,))
        row = cursor.fetchone()
        if row:
            return row[0]
        cursor.execute("INSERT INTO season (year_range) VALUES (%s)", (year_range,))
        conn.commit()
        return cursor.lastrowid

def upsert_player(conn, name: str, nationality: str, position: str) -> int:
    with conn.cursor() as cursor:
        cursor.execute(
            "SELECT id FROM player WHERE name = %s AND primary_position = %s",
            (name, position),
        )
        row = cursor.fetchone()
        if row:
            return row[0]
        cursor.execute(
            """INSERT INTO player (name, nationality, primary_position)
               VALUES (%s, %s, %s)""",
            (name, nationality, position),
        )
        conn.commit()
        return cursor.lastrowid


def upsert_player_season_stat(conn, player_id: int, team_id: int, season_id: int):
    with conn.cursor() as cursor:
        cursor.execute(
            """SELECT id FROM player_season_stat
               WHERE player_id = %s AND team_id = %s AND season_id = %s""",
            (player_id, team_id, season_id),
        )
        if cursor.fetchone():
            return
        cursor.execute(
            """INSERT INTO player_season_stat
               (player_id, team_id, season_id, goals, assists, appearances)
               VALUES (%s, %s, %s, 0, 0, 0)""",
            (player_id, team_id, season_id),
        )
        conn.commit()

def load_players_from_csv(filepath: str, team_name: str) -> list[dict]:
    """CSV에서 특정 팀 소속 선수만 필터링해서 읽어온다."""
    players = []
    with open(filepath, encoding="utf-8") as f:
        reader = csv.DictReader(f)
        for row in reader:
            if row["team_name"] == team_name:
                players.append({
                    "name": row["player_name"],
                    "nationality": row["nationality"],
                    "primary_position": row["primary_position"],
                })

    return players