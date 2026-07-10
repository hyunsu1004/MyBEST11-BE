package com.best11.review.dto.response;


import java.time.LocalDateTime;

public record ReviewResponse(Long id , String content, LocalDateTime createdAt) {
}
