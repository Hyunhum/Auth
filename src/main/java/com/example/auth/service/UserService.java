package com.example.auth.service;

import com.example.auth.dto.*;

public interface UserService {

    public UserInfoDto findUserInfoByUserId(Long userId) throws Exception;

}
