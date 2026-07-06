package com.best11.formation.dto.response;

import java.util.List;

public record formationResponseDto(Long id, String name, List<positionSlotResponseDto>positionLayout){
}
