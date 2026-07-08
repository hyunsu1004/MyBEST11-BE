package com.best11.besteleven.controller;

import com.best11.besteleven.dto.request.BestElevenCreateRequestDto;
import com.best11.besteleven.dto.request.BestElevenUpdateRequestDto;
import com.best11.besteleven.dto.response.BestElevenResponseDto;
import com.best11.besteleven.repository.BestElevenRepository;
import com.best11.besteleven.service.BestElevenService;
import com.best11.common.DTO.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/best11")
@RequiredArgsConstructor
public class BestElevenController {

    private final BestElevenService bestElevenService;

    @PostMapping
    public ApiResponse<BestElevenResponseDto> create(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody BestElevenCreateRequestDto request
            ){
        return ApiResponse.ok(bestElevenService.create(userId,request));
    }

    @GetMapping
    public ApiResponse<List<BestElevenResponseDto>> getMyList(@AuthenticationPrincipal Long userId) {
        return ApiResponse.ok(bestElevenService.getMyList(userId));
    }

    @GetMapping("/{id}")
    public ApiResponse<BestElevenResponseDto> getDatail(@PathVariable Long id){
        return ApiResponse.ok(bestElevenService.getDetail(id));
    }

    @PutMapping("/{id}")
    public ApiResponse<BestElevenResponseDto> update(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long id,
            @Valid @RequestBody BestElevenUpdateRequestDto request
    ){
        return ApiResponse.ok(bestElevenService.update(userId,id,request));
    }
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@AuthenticationPrincipal Long userId,
                                    @PathVariable Long id){
        bestElevenService.delete(userId,id);
        return ApiResponse.ok(null);
    }


}
