package com.best11.review.entity;

import com.best11.besteleven.entity.BestEleven;
import com.best11.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BestElevenReview extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "best_eleven_id",nullable = false)
    private BestEleven bestEleven;

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Builder
    public BestElevenReview(BestEleven bestEleven, String content) {
        this.bestEleven = bestEleven;
        this.content = content;
    }
}
