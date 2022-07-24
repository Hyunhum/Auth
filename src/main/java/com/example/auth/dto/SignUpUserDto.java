package com.example.auth.dto;

import javax.validation.constraints.NotBlank;

import lombok.Getter;
 
@Getter
public class SignUpUserDto {

    @NotBlank
    private String email;

    @NotBlank
    private String nickname;

    @NotBlank
    private String password;

    @NotBlank
    private String name;

    @NotBlank
    private String phoneNum;
/*   
    @Enumerated(EnumType.STRING)
    private UserRole role = UserRole.ROLE_NOT_PERMITTED;
*/
    public SignUpUserDto(String email, String nickname, String password, 
        String name, String phoneNum) {
    
        this.email = email;
        this.nickname = nickname;
        this.password = password;
        this.name = name;
        this.phoneNum = phoneNum;
    }

}
