package com.best11.validation.dto;

import com.best11.validation.entity.ValidationLog;

import java.time.LocalDateTime;
import java.util.List;

public record ValidationRunSummaryDto(
        String runId,
        LocalDateTime startedAt,
        LocalDateTime finishedAt,
        String overallStatus,
        long totalChecks,
        long failCount,
        List<ValidationCheckDto> checks
) {
    public record ValidationCheckDto(
            String step,
            String checkName,
            String status,
            String detail,
            LocalDateTime checkedAt
    ) {
        public static ValidationCheckDto from(ValidationLog log) {
            return new ValidationCheckDto(
                    log.getStep(),
                    log.getCheckName(),
                    log.getStatus().name(),
                    log.getDetail(),
                    log.getCheckedAt()
            );
        }
    }

    public static ValidationRunSummaryDto from(String runId, List<ValidationLog> logs) {
        LocalDateTime started = logs.stream().map(ValidationLog::getCheckedAt).min(LocalDateTime::compareTo).orElse(null);
        LocalDateTime finished = logs.stream().map(ValidationLog::getCheckedAt).max(LocalDateTime::compareTo).orElse(null);
        long failCount = logs.stream().filter(l -> l.getStatus() == ValidationLog.ValidationStatus.FAIL).count();
        long warnCount = logs.stream().filter(l -> l.getStatus() == ValidationLog.ValidationStatus.WARN).count();
        String overall = failCount > 0 ? "FAIL" : (warnCount > 0 ? "WARN" : "PASS");
        List<ValidationCheckDto> checks = logs.stream().map(ValidationCheckDto::from).toList();
        return new ValidationRunSummaryDto(runId, started, finished, overall, logs.size(), failCount, checks);
    }

}
