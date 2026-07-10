package com.best11.review.controller;


import com.best11.common.DTO.ApiResponse;
import com.best11.review.dto.response.ReviewResponse;
import com.best11.review.service.AiReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/best11/{id}/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final AiReviewService aiReviewService;

    @PostMapping
    public ApiResponse<ReviewResponse> create(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long id
    ){
        return ApiResponse.ok(aiReviewService.createReview(userId, id));
    }

    @GetMapping
    public ApiResponse<List<ReviewResponse>> get_list(@PathVariable Long id){
        return ApiResponse.ok(aiReviewService.getReviews(id));
    }

}
