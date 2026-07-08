package com.best11.player.controller;


import com.best11.common.DTO.ApiResponse;
import com.best11.player.dto.PlayerSummaryResponseDto;
import com.best11.player.service.PlayerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/players")
@RequiredArgsConstructor
public class PlayerController {

    private final PlayerService playerService;

    @GetMapping
    public ApiResponse<List<PlayerSummaryResponseDto>> search(
            @RequestParam String search,
            @RequestParam(required = false) String position
    ) {
        return ApiResponse.ok(playerService.search(search, position));
    }
}
