package com.best11.besteleven.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record BestElevenResponseDto(Long id, String title, Long formationId, List<SlotResponseDto> slots, LocalDateTime createdAt) {
}

