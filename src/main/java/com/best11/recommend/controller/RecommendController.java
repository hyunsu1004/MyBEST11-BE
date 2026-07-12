package com.best11.recommend.controller;


import com.best11.common.DTO.ApiResponse;
import com.best11.recommend.dto.RecommendedPlayerResponseDto;
import com.best11.recommend.service.RecommendService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/players/{id}/recommendations")
@RequiredArgsConstructor
public class RecommendController {

    private final RecommendService recommendService;

    @GetMapping("/similar-position")
    public ApiResponse<List<RecommendedPlayerResponseDto>> similarPosition(@PathVariable Long id){
        return ApiResponse.ok(recommendService.recommendSimilarPosition(id));
    }

    @GetMapping("/teammates")
    public ApiResponse<List<RecommendedPlayerResponseDto>> teammates(@PathVariable Long id){
        return ApiResponse.ok(recommendService.recommendTeammates(id));
    }
}
