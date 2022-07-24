package com.example.auth.dto;

import javax.validation.constraints.NotBlank;

import lombok.Getter;
 
@Getter
public class LogInUserDto {

    private String email;

    @NotBlank
    private String password;

    private String phoneNum;

    public LogInUserDto(String email, String password, String phoneNum) {
    
        this.email = email;
        this.password = password;
        this.phoneNum = phoneNum;
    }

}
