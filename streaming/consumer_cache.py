"""
Consumer B - 실시간 집계

match-events 토픽을 구독해서, 경기별 득점/카드 수를 Redis Hash에
실시간으로 누적 반영한다. "지금 이 순간 스코어보드" 조회용 (빠른 조회가 목적,
영구 보관은 Consumer A의 MySQL이 담당).

Redis 키 구조:
  match:{match_id}  (Hash)
    - home_team, away_team, league
    - home_goals, away_goals
    - home_yellow, away_yellow
    - home_red, away_red
    - last_event        (가장 최근 이벤트 설명, 문자열)
    - last_updated       (ISO 타임스탬프)

실행: python consumer_cache.py
(producer.py, consumer_db.py와 별도의 터미널 창에서 계속 켜둬야 함)
"""

import os
import json
import logging
import datetime
import redis
from kafka import KafkaConsumer
from dotenv import load_dotenv

load_dotenv()

logging.basicConfig(level=logging.INFO, format="%(asctime)s [%(levelname)s] %(name)s: %(message)s")
logger = logging.getLogger("streaming.consumer_cache")

KAFKA_BOOTSTRAP_SERVERS = os.environ.get("KAFKA_BOOTSTRAP_SERVERS", "localhost:9092")
TOPIC = "match-events"

REDIS_HOST = os.environ.get("REDIS_HOST", "localhost")
REDIS_PORT = int(os.environ.get("REDIS_PORT", "6379"))

r = redis.Redis(host=REDIS_HOST, port=REDIS_PORT, decode_responses=True)


def redis_key(match_id) -> str:
    return f"match:{match_id}"


def ensure_match_initialized(event: dict):
    """해당 경기의 Redis Hash가 없으면 기본값으로 초기화"""
    key = redis_key(event["match_id"])
    if not r.exists(key):
        r.hset(key, mapping={
            "league": event.get("league") or "",
            "home_team": event.get("home_team") or "",
            "away_team": event.get("away_team") or "",
            "home_goals": 0,
            "away_goals": 0,
            "home_yellow": 0,
            "away_yellow": 0,
            "home_red": 0,
            "away_red": 0,
        })


def apply_event(event: dict):
    key = redis_key(event["match_id"])
    is_home = event.get("team") == event.get("home_team")
    side = "home" if is_home else "away"

    event_type = event.get("type")
    detail = event.get("detail") or ""

    if event_type == "Goal":
        r.hincrby(key, f"{side}_goals", 1)
    elif event_type == "Card" and "Yellow" in detail:
        r.hincrby(key, f"{side}_yellow", 1)
    elif event_type == "Card" and "Red" in detail:
        r.hincrby(key, f"{side}_red", 1)
    # subst(교체)는 스코어보드 집계 대상이 아니므로 카운트하지 않고 last_event로만 반영

    r.hset(key, "last_event", f"{event.get('elapsed')}' {event_type}/{detail} - {event.get('player')}")
    r.hset(key, "last_updated", datetime.datetime.now().isoformat())

    r.expire(key, 60 * 60 * 6)  # 경기 종료 후 6시간 지나면 자동 만료 (오래된 스코어보드가 영구히 남지 않도록)


def run():
    consumer = KafkaConsumer(
        TOPIC,
        bootstrap_servers=KAFKA_BOOTSTRAP_SERVERS,
        group_id="cache-aggregator",    # Consumer A와 다른 group_id -> 서로 영향 없이 독립적으로 같은 메시지를 각자 처리
        auto_offset_reset="earliest",
        value_deserializer=lambda v: json.loads(v.decode("utf-8")),
        key_deserializer=lambda k: k.decode("utf-8") if k else None,
    )

    logger.info("Consumer B(실시간 집계) 시작 - match-events 구독 중")

    try:
        for message in consumer:
            event = message.value
            try:
                ensure_match_initialized(event)
                apply_event(event)
                logger.info(
                    f"[집계 갱신] match_id={event.get('match_id')} "
                    f"{event.get('team')} {event.get('type')}/{event.get('detail')}"
                )
            except Exception as e:
                logger.error(f"집계 실패: {e}, event={event}")
    except KeyboardInterrupt:
        logger.info("Consumer 종료")


if __name__ == "__main__":
    run()