CREATE TABLE IF NOT EXISTS validation_log (
                                              id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                              run_id VARCHAR(50) NOT NULL,
    step VARCHAR(30) NOT NULL,
    check_name VARCHAR(50) NOT NULL,
    status ENUM('PASS', 'FAIL', 'WARN') NOT NULL,
    detail TEXT,
    checked_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    INDEX idx_run_id (run_id),
    INDEX idx_checked_at (checked_at),
    INDEX idx_status (status)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE OR REPLACE VIEW validation_run_summary AS
SELECT
    run_id,
    MIN(checked_at) AS started_at,
    MAX(checked_at) AS finished_at,
    COUNT(*) AS total_checks,
    SUM(CASE WHEN status = 'FAIL' THEN 1 ELSE 0 END) AS fail_count,
    SUM(CASE WHEN status = 'WARN' THEN 1 ELSE 0 END) AS warn_count,
    CASE
        WHEN SUM(CASE WHEN status = 'FAIL' THEN 1 ELSE 0 END) > 0 THEN 'FAIL'
        WHEN SUM(CASE WHEN status = 'WARN' THEN 1 ELSE 0 END) > 0 THEN 'WARN'
        ELSE 'PASS'
        END AS overall_status
FROM validation_log
GROUP BY run_id;