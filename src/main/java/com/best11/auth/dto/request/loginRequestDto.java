package com.best11.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record loginRequestDto(@NotBlank @Email String email, @NotBlank String password) {

}
