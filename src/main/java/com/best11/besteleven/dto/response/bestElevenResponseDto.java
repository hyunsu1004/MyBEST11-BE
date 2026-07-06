package com.best11.besteleven.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record bestElevenResponseDto(Long id, String title, Long formationId, List<slotResponseDto> slots, LocalDateTime createdAt) {
}

