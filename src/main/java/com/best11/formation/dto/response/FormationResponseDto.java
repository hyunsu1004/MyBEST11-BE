package com.best11.formation.dto.response;

import java.util.List;

public record FormationResponseDto(Long id, String name, List<PositionSlotResponseDto>positionLayout){
}
