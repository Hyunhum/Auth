package com.example.auth.service;

import com.example.auth.dto.*;

import javax.servlet.http.Cookie;

public interface UserService {

    public UserInfoDto findUserInfoByRefreshToken(String refreshToken) throws Exception;

}
