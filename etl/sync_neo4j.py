from neo4j import GraphDatabase
import pymysql
from config import DB_CONFIG, NEO4J_CONFIG


def sync():
    mysql_conn = pymysql.connect(**DB_CONFIG)
    driver = GraphDatabase.driver(NEO4J_CONFIG["uri"],
                                  auth=(NEO4J_CONFIG["user"], NEO4J_CONFIG["password"]))

    with mysql_conn.cursor() as cursor:
        cursor.execute("SELECT id, name, country FROM league")
        leagues = cursor.fetchall()

        cursor.execute("SELECT id, name, league_id FROM team")
        teams = cursor.fetchall()

        cursor.execute("SELECT id, name, primary_position FROM player")
        players = cursor.fetchall()

        cursor.execute("SELECT player_id, team_id FROM player_season_stat")
        player_teams = cursor.fetchall()

    with driver.session() as session:
        session.run("MATCH (n) DETACH DELETE n")  # 초기화 후 재적재

        for league_id, name, country in leagues:
            session.run(
                "CREATE (:League {id: $id, name: $name, country: $country})",
                id=league_id, name=name, country=country
            )

        for team_id, name, league_id in teams:
            session.run("""
                MATCH (l:League {id: $league_id})
                CREATE (t:Team {id: $team_id, name: $name})-[:COMPETES_IN]->(l)
            """, team_id=team_id, name=name, league_id=league_id)

        for player_id, name, position in players:
            session.run(
                "CREATE (:Player {id: $id, name: $name, position: $position})",
                id=player_id, name=name, position=position
            )

        for player_id, team_id in player_teams:
            session.run("""
                MATCH (p:Player {id: $player_id}), (t:Team {id: $team_id})
                MERGE (p)-[:PLAYS_FOR]->(t)
            """, player_id=player_id, team_id=team_id)

    driver.close()
    mysql_conn.close()
    print("Neo4j 동기화 완료")

if __name__ == "__main__":
    sync()
