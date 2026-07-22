"""
실시간 경기 이벤트 Producer

라이브 경기를 주기적으로 폴링해서, 새로 발생한 이벤트(득점/카드/교체)를
Kafka topic(match-events)으로 발행한다.

두 가지 모드:
- USE_MOCK_DATA=true  : 진짜 API 호출 없이 가짜 이벤트를 주기적으로 생성 (개발/테스트용, 쿼터 소모 없음)
- USE_MOCK_DATA=false : 실제 API-Football을 호출 (쿼터 소모, 시연/데모용)

실행: python producer.py
"""

import os
import time
import json
import random
import logging
import requests
from kafka import KafkaProducer
from dotenv import load_dotenv

load_dotenv()

logging.basicConfig(level=logging.INFO, format="%(asctime)s [%(levelname)s] %(name)s: %(message)s")
logger = logging.getLogger("streaming.producer")

API_KEY = os.environ.get("FOOTBALL_API_KEY")
API_BASE = "https://v3.football.api-sports.io"
KAFKA_BOOTSTRAP_SERVERS = os.environ.get("KAFKA_BOOTSTRAP_SERVERS", "localhost:9092")
TOPIC = "match-events"

USE_MOCK_DATA = os.environ.get("USE_MOCK_DATA", "true").lower() == "true"
POLL_INTERVAL_SECONDS = int(os.environ.get("POLL_INTERVAL_SECONDS", "15" if USE_MOCK_DATA else "300"))

producer = KafkaProducer(
    bootstrap_servers=KAFKA_BOOTSTRAP_SERVERS,
    key_serializer=lambda k: str(k).encode("utf-8"),
    value_serializer=lambda v: json.dumps(v, ensure_ascii=False).encode("utf-8"),
)

seen_events = set()  # 이미 발행한 이벤트를 추적해서 중복 발행 방지


# ==================== 실제 API 모드 ====================

def fetch_live_fixtures():
    headers = {"x-apisports-key": API_KEY}
    resp = requests.get(f"{API_BASE}/fixtures", headers=headers, params={"live": "all"}, timeout=10)
    resp.raise_for_status()
    return resp.json().get("response", [])


def fetch_fixture_events(fixture_id):
    headers = {"x-apisports-key": API_KEY}
    resp = requests.get(f"{API_BASE}/fixtures/events", headers=headers, params={"fixture": fixture_id}, timeout=10)
    resp.raise_for_status()
    return resp.json().get("response", [])


def event_key(fixture_id, event):
    return (
        fixture_id,
        event.get("time", {}).get("elapsed"),
        event.get("type"),
        event.get("detail"),
        (event.get("player") or {}).get("id"),
    )


def build_payload_from_api(fixture_id, event, fixture_meta):
    return {
        "match_id": fixture_id,
        "league": fixture_meta["league"]["name"],
        "home_team": fixture_meta["teams"]["home"]["name"],
        "away_team": fixture_meta["teams"]["away"]["name"],
        "elapsed": event.get("time", {}).get("elapsed"),
        "type": event.get("type"),
        "detail": event.get("detail"),
        "team": (event.get("team") or {}).get("name"),
        "player": (event.get("player") or {}).get("name"),
        "assist": (event.get("assist") or {}).get("name"),
    }


def poll_real_api():
    fixtures = fetch_live_fixtures()
    if not fixtures:
        logger.info("현재 진행 중인 라이브 경기 없음")
        return

    for fixture in fixtures:
        fixture_id = fixture["fixture"]["id"]
        events = fetch_fixture_events(fixture_id)
        for event in events:
            key = event_key(fixture_id, event)
            if key not in seen_events:
                seen_events.add(key)
                payload = build_payload_from_api(fixture_id, event, fixture)
                publish(fixture_id, payload)


# ==================== Mock 모드 (개발/테스트용) ====================

MOCK_MATCHES = [
    {"match_id": 9001, "home_team": "Manchester City", "away_team": "Arsenal", "league": "Premier League"},
    {"match_id": 9002, "home_team": "Liverpool", "away_team": "Chelsea", "league": "Premier League"},
]
MOCK_EVENT_TYPES = [
    ("Goal", "Normal Goal"),
    ("Card", "Yellow Card"),
    ("Card", "Red Card"),
    ("subst", "Substitution 1"),
]
_mock_elapsed = {m["match_id"]: 0 for m in MOCK_MATCHES}


def poll_mock_data():
    for match in MOCK_MATCHES:
        # 매 폴링마다 1~3분 정도 경기 시간이 흐른 것처럼 시뮬레이션
        _mock_elapsed[match["match_id"]] += random.randint(1, 3)
        elapsed = _mock_elapsed[match["match_id"]]
        if elapsed > 90:
            continue  # 경기 종료로 간주, 더 이상 이벤트 안 냄

        # 매 폴링마다 30% 확률로 이벤트 하나 발생
        if random.random() < 0.3:
            event_type, detail = random.choice(MOCK_EVENT_TYPES)
            payload = {
                "match_id": match["match_id"],
                "league": match["league"],
                "home_team": match["home_team"],
                "away_team": match["away_team"],
                "elapsed": elapsed,
                "type": event_type,
                "detail": detail,
                "team": random.choice([match["home_team"], match["away_team"]]),
                "player": f"Player {random.randint(1, 23)}",
                "assist": None,
            }
            publish(match["match_id"], payload)


# ==================== 공통 ====================

def publish(match_id, payload):
    producer.send(TOPIC, key=match_id, value=payload)
    logger.info(f"[발행] match_id={match_id} {payload['type']}/{payload['detail']} - {payload['player']}")


def run():
    mode = "MOCK" if USE_MOCK_DATA else "REAL API"
    logger.info(f"실시간 경기 이벤트 Producer 시작 (모드: {mode}, 폴링 간격: {POLL_INTERVAL_SECONDS}초)")

    while True:
        try:
            if USE_MOCK_DATA:
                poll_mock_data()
            else:
                poll_real_api()
            producer.flush()
        except Exception as e:
            logger.error(f"폴링 중 오류 발생: {e}")

        time.sleep(POLL_INTERVAL_SECONDS)


if __name__ == "__main__":
    run()