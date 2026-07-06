package com.best11.besteleven.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record slotRequestDto(@NotBlank String positionCode, @NotNull Long playerId) {
}
