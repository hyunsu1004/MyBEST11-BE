package com.best11.formation.controller;

import com.best11.common.DTO.ApiResponse;
import com.best11.formation.dto.response.FormationResponseDto;
import com.best11.formation.service.FormationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/formations")
@RequiredArgsConstructor
public class FormationController {

    private final FormationService formationService;

    @GetMapping
    public ApiResponse<List<FormationResponseDto>> getAllFormations() {
        return ApiResponse.ok(formationService.getAllFormations());
    }
}
