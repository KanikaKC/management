package com.finance.login.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ApiResponseDto<T> {
    private boolean success;
    private T data;
    private String error;
    private int statusCode;
    private String token;

    public ApiResponseDto(boolean success, T data, String error, int statusCode, String token) {
        this.success = success;
        this.data = data;
        this.error = error;
        this.statusCode = statusCode;
        this.token = token;
    }
}

