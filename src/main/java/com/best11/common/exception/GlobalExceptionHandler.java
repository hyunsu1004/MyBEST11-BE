package com.best11.common.exception;

import com.best11.common.DTO.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponse<Void>> handleCustomException(CustomException e) {
        return ResponseEntity
                .status(e.getErrorCode().getStatus())
                .body(ApiResponse.error(e.getMessage()));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadCredentials(BadCredentialsException e) {
        return ResponseEntity.status(401).body(ApiResponse.error(e.getMessage()));
    }

    //    @ExceptionHandler(Exception.class)
//    public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
//        return ResponseEntity
//                .status(500)
//                .body(ApiResponse.error("서버 내부 오류가 발생했습니다."));
//    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
        System.out.println("=== EXCEPTION CAUGHT ===");
        System.out.println(e.getClass().getName());
        System.out.println(e.getMessage());
        e.printStackTrace();
        return ResponseEntity
                .status(500)
                .body(ApiResponse.error("서버 내부 오류가 발생했습니다."));
    }
}