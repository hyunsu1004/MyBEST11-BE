package com.best11.formation.service;


import com.best11.common.exception.CustomException;
import com.best11.formation.dto.response.FormationResponseDto;
import com.best11.formation.dto.response.PositionSlotResponseDto;
import com.best11.formation.entity.Formation;
import com.best11.formation.repository.FormationRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FormationService {

    private final FormationRepository formationRepository;
    private final ObjectMapper objectMapper;

    public List<FormationResponseDto> getAllFormations() {
        return formationRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    private FormationResponseDto toResponse(Formation formation) {
        try {
            List<PositionSlotResponseDto> layout = objectMapper.readValue(
                    formation.getPositionLayout(), new TypeReference<List<PositionSlotResponseDto>>(){}
            );
            return new FormationResponseDto(formation.getId(),formation.getName(),layout);
        }catch (JsonProcessingException e){
            throw new CustomException("포메이션 데이터 파싱 오류",e);
        }
    }
}
