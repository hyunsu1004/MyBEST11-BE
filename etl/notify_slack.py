import os
import json
import urllib.request
from typing import List, Tuple

SLACK_WEBHOOK_URL = os.environ.get("SLACK_WEBHOOK_URL")


def send_validation_alert(run_id: str, step: str, failures: List[Tuple[str, str]]):
    if not SLACK_WEBHOOK_URL:
        print("[알림 생략] SLACK_WEBHOOK_URL 환경변수가 설정되지 않았습니다.")
        return

    failure_lines = "\n".join([f"• *{name}*: {detail}" for name, detail in failures])
    payload = {
        "text": (
            f":x: *데이터 품질 검증 실패* (`{step}`)\n"
            f"run_id: `{run_id}`\n\n"
            f"{failure_lines}"
        )
    }
    req = urllib.request.Request(
        SLACK_WEBHOOK_URL,
        data=json.dumps(payload).encode("utf-8"),
        headers={"Content-Type": "application/json"},
        method="POST",
    )
    try:
        with urllib.request.urlopen(req, timeout=5) as response:
            if response.status != 200:
                print(f"[알림 실패] Slack 응답 코드: {response.status}")
    except Exception as e:
        print(f"[알림 실패] Slack 전송 중 오류: {e}")