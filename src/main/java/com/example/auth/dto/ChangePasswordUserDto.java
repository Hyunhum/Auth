package com.example.auth.dto;

import javax.validation.constraints.NotBlank;

import lombok.Getter;
 
@Getter
public class ChangePasswordUserDto {

    @NotBlank
    private String password;
    
    @NotBlank
    private String phoneNum;

    public ChangePasswordUserDto(String password, String phoneNum) {

        this.password = password;
        this.phoneNum = phoneNum;
    }

}
