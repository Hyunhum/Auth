package com.example.auth.dto;

import javax.validation.constraints.NotBlank;

import javax.persistence.Enumerated;
import javax.persistence.EnumType;

import lombok.Getter;

import com.example.auth.config.UserRole;
import com.example.auth.dao.entity.User;

@Getter
public class SecurityUserDto {
    
    @NotBlank
    private String password;

    @NotBlank
    private String phoneNum;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    public SecurityUserDto(String password, String phoneNum, UserRole role) {
        this.password = password;
        this.phoneNum = phoneNum;
        this.role = role;
    }

    public static SecurityUserDto from(User user) {
        return new SecurityUserDto(user.getPassword(), 
        user.getPhoneNum(), user.getRole());
    }

}
