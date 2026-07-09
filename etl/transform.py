#API 응답을 우리 스키마에 맞게 변환(정제)

"""football-data.org의 포지션 분류(Goalkeeper/Defence/Midfield/Offence)가
우리 formation의 세밀한 포지션 코드(GK/CB/LB/CM/ST 등)보다 훨씬 뭉뚱그려져 있음.
지금은 대분류 → 대표 코드 하나로 단순 매핑했는데,
나중에 여유 되면 수동으로 세부 포지션을 보정하거나 다른 API를 보조로 쓰는 걸 고려해보기"""

def transform_team(raw_team:dict) ->dict:
    return {
    "api-team_id" : raw_team["id"],
    "name" :  raw_team["name"],
    }

def transform_players(raw_team: dict)-> list[dict]:
    players = []
    for squad_member in raw_team.get("squad",[]):
        position_map = {
        "GoalKeeper": "GK",
        "Defense" : "CB",
        "Midfield": "CM",
        "Offence" : "ST",
        }
        players.append({
        "name" : squad_member["name"],
        "nationality" : squad_member.get["nationality"],
        "primary_position" : position_map.get(squad_member.get("position"),"CM")
        })

    return players

def transform_teams_from_standings(standings_data: dict) -> list[dict]:
    """standings[0]['table']에서 팀 목록을 뽑아낸다 (TOTAL 기준 테이블 하나만 사용)."""
    table = standings_data["standings"][0]["table"]
    teams = []
    for entry in table:
        teams.append({
            "name": entry["team"]["name"],
        })
    return teams