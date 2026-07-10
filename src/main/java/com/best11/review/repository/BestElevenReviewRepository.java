package com.best11.review.repository;


import com.best11.review.entity.BestElevenReview;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BestElevenReviewRepository extends JpaRepository<BestElevenReview, Long> {
    List<BestElevenReview> findByBestElevenIdOrderByCreatedAtDesc(Long bestElevenId);
}
