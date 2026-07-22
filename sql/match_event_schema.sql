-- 실시간 경기 이벤트 원본 저장 테이블
-- Kafka Consumer A(consumer_db.py)가 이 테이블에 직접 INSERT
-- validation_log와 마찬가지로 Hibernate ddl-auto 관리 대상 밖에 둘 것을 권장

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
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;