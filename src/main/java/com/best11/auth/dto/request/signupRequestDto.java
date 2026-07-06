package com.best11.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record signupRequestDto(
        @NotBlank @Size(min = 2 , max = 20) String username,
        @NotBlank @Email String email, @NotBlank @Size(min=8) String password
) {}


