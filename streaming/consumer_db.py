"""
Consumer A - 원본 데이터 적재

match-events 토픽을 구독해서, 들어오는 이벤트를 가공 없이 그대로
MySQL match_event 테이블에 적재한다. 나중에 재조회/분석 가능한 영구 기록용.

실행: python consumer_db.py
(producer.py와 별도의 터미널 창에서 계속 켜둬야 함 - 독립적인 장시간 실행 프로세스)
"""

import os
import re
import json
import logging
import pymysql
from kafka import KafkaConsumer
from dotenv import load_dotenv

load_dotenv()

logging.basicConfig(level=logging.INFO, format="%(asctime)s [%(levelname)s] %(name)s: %(message)s")
logger = logging.getLogger("streaming.consumer_db")

KAFKA_BOOTSTRAP_SERVERS = os.environ.get("KAFKA_BOOTSTRAP_SERVERS", "localhost:9092")
TOPIC = "match-events"


def parse_db_config_from_url():
    """
    기존 .env가 Spring Boot 형식(JDBC URL)을 쓰므로 그대로 재사용한다.
    예: DB_URL=jdbc:mysql://maglev.proxy.rlwy.net:36323/railway
    """
    db_url = os.environ.get("DB_URL", "")
    match = re.match(r"jdbc:mysql://([^:/]+):(\d+)/([^?]+)", db_url)

    if not match:
        raise ValueError(
            f"DB_URL 형식을 해석할 수 없습니다: '{db_url}'. "
            f".env에 DB_URL=jdbc:mysql://호스트:포트/DB이름 형식이 있는지 확인해주세요."
        )
    host, port, database = match.groups()
    return {
        "host": host,
        "port": int(port),
        "user": os.environ.get("DB_USERNAME"),
        "password": os.environ.get("DB_PASSWORD"),
        "database": database,
        "charset": "utf8mb4",
    }


DB_CONFIG = parse_db_config_from_url()


def get_connection():
    return pymysql.connect(**DB_CONFIG)


def create_table_if_not_exists(conn):
    with conn.cursor() as cursor:
        cursor.execute("""
            CREATE TABLE IF NOT EXISTS match_event (
                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                match_id BIGINT NOT NULL,
                league VARCHAR(100),
                home_team VARCHAR(100),
                away_team VARCHAR(100),
                elapsed INT,
                event_type VARCHAR(30),
                event_detail VARCHAR(50),
                team VARCHAR(100),
                player VARCHAR(100),
                assist VARCHAR(100),
                received_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                INDEX idx_match_id (match_id),
                INDEX idx_received_at (received_at)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
        """)
    conn.commit()


def insert_event(conn, event: dict):
    with conn.cursor() as cursor:
        cursor.execute(
            """
            INSERT INTO match_event
                (match_id, league, home_team, away_team, elapsed, event_type, event_detail, team, player, assist)
            VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s)
            """,
            (
                event.get("match_id"),
                event.get("league"),
                event.get("home_team"),
                event.get("away_team"),
                event.get("elapsed"),
                event.get("type"),
                event.get("detail"),
                event.get("team"),
                event.get("player"),
                event.get("assist"),
            ),
        )
    conn.commit()


def run():
    conn = get_connection()
    create_table_if_not_exists(conn)

    consumer = KafkaConsumer(
        TOPIC,
        bootstrap_servers=KAFKA_BOOTSTRAP_SERVERS,
        group_id="db-writer",          # 이 group_id로 처리 위치(offset)가 별도 관리됨
        auto_offset_reset="earliest",   # 처음 실행 시 토픽의 가장 오래된 메시지부터 읽음
        value_deserializer=lambda v: json.loads(v.decode("utf-8")),
        key_deserializer=lambda k: k.decode("utf-8") if k else None,
    )

    logger.info("Consumer A(DB 적재) 시작 - match-events 구독 중")

    try:
        for message in consumer:
            event = message.value
            try:
                insert_event(conn, event)
                logger.info(
                    f"[적재] match_id={event.get('match_id')} "
                    f"{event.get('type')}/{event.get('detail')} - {event.get('player')}"
                )
            except Exception as e:
                logger.error(f"적재 실패: {e}, event={event}")
    except KeyboardInterrupt:
        logger.info("Consumer 종료")
    finally:
        conn.close()


if __name__ == "__main__":
    run()