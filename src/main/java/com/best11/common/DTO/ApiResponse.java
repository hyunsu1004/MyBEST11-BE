package com.best11.common.DTO;

public record ApiResponse<T>(boolean success, T data, String errorMessage){
    public static <T> ApiResponse<T> ok(T data){
        return new ApiResponse<>(true,data,null);
    }
    public static <T> ApiResponse<T> error(String message){
        return new ApiResponse<>(false,null,message);
    }
}

