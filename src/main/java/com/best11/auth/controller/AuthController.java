package com.best11.auth.controller;

import com.best11.auth.dto.request.LoginRequestDto;
import com.best11.auth.dto.request.SignupRequestDto;
import com.best11.auth.dto.response.LoginResponseDto;
import com.best11.auth.service.AuthService;
import com.best11.common.DTO.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/signup")
    public ApiResponse<Void> signup(@Valid @RequestBody SignupRequestDto request) {
        authService.signUp(request);
        return ApiResponse.ok(null);
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto request) {

        return ApiResponse.ok(authService.login(request));
    }

}
