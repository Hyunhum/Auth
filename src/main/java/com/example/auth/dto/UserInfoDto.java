package com.example.auth.dto;

import javax.validation.constraints.NotBlank;

import lombok.Getter;

import com.example.auth.dao.entity.User;
 
@Getter
public class UserInfoDto {

    @NotBlank
    private String email;

    @NotBlank
    private String nickname;

    @NotBlank
    private String name;

    @NotBlank
    private String phoneNum;

    public UserInfoDto(String email, String nickname, 
        String name, String phoneNum) {
    
        this.email = email;
        this.nickname = nickname;
        this.name = name;
        this.phoneNum = phoneNum;
    }

    public static UserInfoDto from(User user) {

        return new UserInfoDto(
            user.getEmail(),
            user.getNickname(),
            user.getName(), 
            user.getPhoneNum());

    }

}