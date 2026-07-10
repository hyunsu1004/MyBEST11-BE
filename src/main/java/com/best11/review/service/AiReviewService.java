package com.best11.review.service;

import com.best11.besteleven.entity.BestEleven;
import com.best11.besteleven.entity.BestElevenSlot;
import com.best11.besteleven.repository.BestElevenRepository;
import com.best11.common.exception.CustomException;
import com.best11.common.exception.ErrorCode;
import com.best11.review.dto.request.AnthropicClient;
import com.best11.review.dto.response.ReviewResponse;
import com.best11.review.entity.BestElevenReview;
import com.best11.review.repository.BestElevenReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AiReviewService {

    private final BestElevenReviewRepository bestReviewRepository;
    private final BestElevenRepository bestElevenRepository;
    private final AnthropicClient anthropicClient;

    @Transactional
    public ReviewResponse createReview(Long userId, Long bestElevenId) {
        BestEleven bestEleven = bestElevenRepository.findByIdWithSlots(bestElevenId)
                .orElseThrow(() -> new CustomException(ErrorCode.BEST_ELEVEN_NOT_FOUND));

        if (!bestEleven.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        String prompt = buildPrompt(bestEleven);
        String aiComment = anthropicClient.sendMessage(prompt);

        BestElevenReview review = bestReviewRepository.save(
                BestElevenReview.builder().bestEleven(bestEleven).content(aiComment).build());

        return new ReviewResponse(review.getId(), review.getContent(), review.getCreatedAt());

    }

    @Transactional(readOnly = true)
    public List<ReviewResponse> getReviews(Long bestElevenId) {
        return bestReviewRepository.findByBestElevenIdOrderByCreatedAtDesc(bestElevenId).stream()
                .map(r -> new ReviewResponse(r.getId(), r.getContent(), r.getCreatedAt()))
                .toList();
    }

    private String buildPrompt(BestEleven bestEleven) {
        StringBuilder sb = new StringBuilder();
        sb.append("다음은 사용자가 구성한 축구 베스트 11 라인업입니다.\n");
        sb.append("포메이션: ").append(bestEleven.getFormation().getName()).append("\n");
        sb.append("라인업:\n");
        for (BestElevenSlot slot : bestEleven.getSlots()) {
            sb.append("- ").append(slot.getPositionCode())
                    .append(": ").append(slot.getPlayer().getName())
                    .append(" (").append(slot.getPlayer().getNationality())
                    .append(", ").append(slot.getPlayer().getPrimaryPosition()).append(")\n");
        }
        sb.append("\n이 라인업의 전술적 밸런스, 강점과 약점, 선수 조합의 케미스트리를 ")
                .append("친근하고 흥미로운 톤으로 3~4문장으로 평가해줘.");
        return sb.toString();
    }
}
