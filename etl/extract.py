import time
import requests
from config import API_KEY, API_BASE_URL
print("API_KEY loaded : ",API_KEY[:5] + "..." if API_KEY else "None")

HEADERS = {"X-Auth_Token" : API_KEY}

#extract.py — API에서 원본 데이터 수집

# def fetch_competition_teams(competition_code : str) -> dict:
#     #특정 대회 팀 목록 + 선수단 정보를 가져옴
#     url = f"{API_BASE_URL}/competitions/{competition_code}/standings"
#     response = requests.get(url,headers = HEADERS)
# #     print("Status:", response.status_code) 디버깅용
# #     print("Body:", response.text)
#     response.raise_for_status()
#     return response.json()
#
# def transform_teams_from_standings(standings_data: dict) -> list[dict]:
#     """standings[0]['table']에서 팀 목록을 뽑아낸다 (TOTAL 기준 테이블 하나만 사용)."""
#     table = standings_data["standings"][0]["table"]
#     teams = []
#     for entry in table:
#         teams.append({
#             "name": entry["team"]["name"],
#         })
#     return teams


def fetch_team_squad(team_id : int) ->dict:
    #팀 하나의 상세 선수단 정보를 가져온다.
    url = f"{API_BASE_URL}/teams/{team_id}"
    response = requests.get(url,headers=HEADERS)
    response.raise_for_status()
    time.sleep(6) #무료 티어는 분당 10회 제한 -> 요청 간 텀을 준다.HEADERS
    return response.json()

def fetch_standings(competition_code: str) -> dict:
    """무료 티어에 포함된 순위표 엔드포인트 - 팀 목록을 여기서 추출한다."""
    url = f"{API_BASE_URL}/competitions/{competition_code}/standings"
    response = requests.get(url, headers=HEADERS)
    print("Status:", response.status_code) #디버깅용
    print("Body:", response.text)
    response.raise_for_status()
    return response.json()