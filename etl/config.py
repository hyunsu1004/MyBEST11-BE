import os
from dotenv import load_dotenv

load_dotenv()

#config.py — 설정 로드

API_KEY = os.getenv("FOOTBALL_DATA_API_KEY")
API_BASE_URL = "https://api.football-data.org/v4"

DB_CONFIG = {
     "host": os.getenv("DB_HOST"),
     "port": int(os.getenv("DB_PORT", 3306)),
     "database": os.getenv("DB_NAME"),
     "user": os.getenv("DB_USER"),
     "password": os.getenv("DB_PASSWORD"),
     "charset": "utf8mb4",
}

NEO4J_CONFIG = {
     "uri": os.getenv("NEO4J_URI"),
     "user": os.getenv("NEO4J_USER"),
     "password": os.getenv("NEO4J_PASSWORD"),
}

#football-data-.org 무료 티어에서 지원하는 대회 코드
COMPETITIONS = {
     "PL": "Premier League",
}