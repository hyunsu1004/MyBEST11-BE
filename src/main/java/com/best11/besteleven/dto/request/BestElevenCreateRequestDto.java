package com.best11.besteleven.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record BestElevenCreateRequestDto(@NotNull Long formationId, @NotBlank String title, @NotEmpty @Size(min = 11,max = 11)
List<SlotRequestDto> slots) {
}
