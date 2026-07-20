package com.best11.validation.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "validation_log")
@Getter
@NoArgsConstructor
public class ValidationLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "run_id",nullable = false,length = 50)
    private String runId;


    @Column(name = "step",nullable = false,length = 30)
    private String step;

    @Column(name = "check_name", nullable = false, length = 50)
    private String checkName;

    @Enumerated(EnumType.STRING)
    @Column(name = "status",nullable = false)
    private ValidationStatus status;

    @Column(name = "detail", columnDefinition = "TEXT")
    private String detail;

    @Column(name = "checked_at", nullable = false)
    private LocalDateTime checkedAt;

    public enum ValidationStatus {
        PASS, WARN, FAIL
    }

}
