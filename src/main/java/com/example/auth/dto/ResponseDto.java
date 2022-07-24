package com.example.auth.dto;

import javax.validation.constraints.NotBlank;

import lombok.Getter;

@Getter
public class ResponseDto {
    
    @NotBlank
    private String code;

    @NotBlank
    private String message;

    public ResponseDto(String code, String message) {
        this.code = code;
        this.message = message;
    }

}