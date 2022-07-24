package com.example.auth.service;

import com.example.auth.dto.*;

public interface AuthService {

    ResponseDto signUpUser(SignUpUserDto userDto) throws Exception;

    LogInUserDto logInUser(LogInUserDto userDto) throws Exception;

    ResponseDto changePasswordUser(ChangePasswordUserDto userDto) throws Exception;

}