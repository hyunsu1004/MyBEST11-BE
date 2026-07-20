package com.best11.validation.controller;


import com.best11.validation.dto.ValidationRunSummaryDto;
import com.best11.validation.entity.ValidationLog;
import com.best11.validation.repository.ValidationLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/validation")
@RequiredArgsConstructor
public class ValidationLogController {

    private final ValidationLogRepository validationLogRepository;

    @GetMapping("/runs")
    public List<String> getRecentRunIds() {
        return validationLogRepository.findRecentRunIds();
    }

    @GetMapping("/runs/{runId}")
    public ValidationRunSummaryDto getRunDetail(@PathVariable String runId) {
        List<ValidationLog> logs = validationLogRepository.findByRunIdOrderByCheckedAtAsc(runId);
        return ValidationRunSummaryDto.from(runId, logs);
    }

    @GetMapping("/failures")
    public List<ValidationRunSummaryDto.ValidationCheckDto> getRecentFailures() {
        return validationLogRepository.findRecentFailures().stream()
                .map(ValidationRunSummaryDto.ValidationCheckDto::from)
                .toList();
    }

    @GetMapping("/status")
    public Map<String, Object> getOverallStatus() {
        List<ValidationLog> recent = validationLogRepository.findTop50ByOrderByCheckedAtDesc();
        long failCount = recent.stream().filter(l -> l.getStatus() == ValidationLog.ValidationStatus.FAIL).count();
        return Map.of(
                "recentCheckCount", recent.size(),
                "recentFailCount", failCount,
                "status", failCount > 0 ? "FAIL" : "PASS"
        );
    }

}
