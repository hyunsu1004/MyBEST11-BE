package com.best11.validation.repository;


import com.best11.validation.entity.ValidationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ValidationLogRepository extends JpaRepository<ValidationLog, Long> {

    List<ValidationLog> findByRunIdOrderByCheckedAtAsc(String runId);

    List<ValidationLog> findTop50ByOrderByCheckedAtDesc();

    @Query("SELECT DISTINCT v.runId FROM ValidationLog v ORDER BY MAX(v.checkedAt) DESC")
    List<String> findRecentRunIds();

    @Query("SELECT v FROM ValidationLog v WHERE v.status = 'FAIL' ORDER BY v.checkedAt DESC")
    List<ValidationLog> findRecentFailures();
}
