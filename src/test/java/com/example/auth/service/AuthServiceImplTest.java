package com.example.auth.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.commons.util.ReflectionUtils;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.HashSet;

import com.example.auth.config.*;
import com.example.auth.service.*;
import com.example.auth.service.util.*;
import com.example.auth.service.impl.*;
import com.example.auth.dao.entity.*;
import com.example.auth.dao.repository.*;
import com.example.auth.dto.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Spy
    private SaltUtil saltUtil;
    
    @InjectMocks
    public AuthServiceImpl authService;

    @Test
    @DisplayName("signup user test")
    void signUpUser() throws Exception {

        //1. when user is present
        //given
        SignUpUserDto userDto = new SignUpUserDto("email", "nickname", "password", "name", "phoneNum");
        User user = new User("email", "nickname", "password", "name", "phoneNum", 
        UserRole.ROLE_USER, new Salt(saltUtil.genSalt()));
        //when
        when(userRepository.findByPhoneNum("phoneNum")).thenReturn(
            Optional.ofNullable(user));
        //then
        try {
            authService.signUpUser(userDto);
        } catch (Exception e) {
            assertThat(e.getMessage()).isEqualTo("이미 등록된 유저입니다");
        }

        //2. when user is not present but phoneNum is invalid
        //given
        SignUpUserDto userDtoPhoneNum = new SignUpUserDto("email", "nickname", "password", "name", "phoneNum");
        //when
        when(userRepository.findByPhoneNum("phoneNum")).thenReturn(
            Optional.ofNullable(null));
        //then
        try {
            authService.signUpUser(userDtoPhoneNum);
        } catch (Exception e) {
            assertThat(e.getMessage()).isEqualTo("전화번호 형식이 올바르지 않습니다");
        }
        //3. when user is not present but email is invalid
        //given
        SignUpUserDto userDtoEmail = new SignUpUserDto("email", "nickname", "password", "name", "01012345678");
        //when
        when(userRepository.findByPhoneNum("01012345678")).thenReturn(
            Optional.ofNullable(null));
        //then
        try {
            authService.signUpUser(userDtoEmail);
        } catch (Exception e) {
            assertThat(e.getMessage()).isEqualTo("이메일 형식이 올바르지 않습니다");
        }
        //4. when user is not present but password is invalid
        //given
        SignUpUserDto userDtoPassword = new SignUpUserDto("abc@naver.com", "nickname", "password!@#", "name", "01012345678");
        //then
        try {
            authService.signUpUser(userDtoPassword);
        } catch (Exception e) {
            assertThat(e.getMessage()).isEqualTo("비밀번호 형식이 올바르지 않습니다");
        }
        //4. when user is not present but name is invalid
        //given
        SignUpUserDto userDtoName = new SignUpUserDto("abc@naver.com", "nickname", "password", "name!@#", "01012345678");
        //then
        try {
            authService.signUpUser(userDtoName);
        } catch (Exception e) {
            assertThat(e.getMessage()).isEqualTo("이름 형식이 올바르지 않습니다");
        }
        //5. when user is not present but all fields are valid
        //given
        SignUpUserDto userDtoValid = new SignUpUserDto("abc@naver.com", "nickname", "password", "heum", "01012345678");
        //when
        ResponseDto responseDto = authService.signUpUser(userDtoValid);
        //then
        assertThat(responseDto.getCode()).isEqualTo("200");
        assertThat(responseDto.getMessage()).isEqualTo("회원가입이 완료되었습니다");      

    }

    @Test
    @DisplayName("login user test")
    void logInUser() throws Exception {

        //1. when password different
        //given
        LogInUserDto userDto =  new LogInUserDto("email", "1", "phoneNum");
        User user = new User("email", "nickname", "password", "name", "phoneNum", 
        UserRole.ROLE_USER, new Salt(saltUtil.genSalt()));
        //when
        when(userRepository.findByEmail("email")).thenReturn(
            Optional.ofNullable(user));
        //then
        try {
            authService.logInUser(userDto);
        } catch (Exception e) {
            assertThat(e.getMessage()).isEqualTo("비밀번호가 틀립니다.");
        }

        //2. when email present and password same
        // given
        LogInUserDto userDtoEmail =  new LogInUserDto("email", "password", "phoneNum");
        Salt salt = new Salt(saltUtil.genSalt());
        User userEmail = new User("email", "nickname", 
        saltUtil.encodePassword(salt.getSalt(), "password"), "name", "phoneNum", 
        UserRole.ROLE_USER, salt);
        //when
        when(userRepository.findByEmail("email")).thenReturn(
            Optional.ofNullable(userEmail));
        //then
        LogInUserDto userDtoForJwt = authService.logInUser(userDtoEmail);
        assertThat(userDtoForJwt.getEmail()).isEqualTo(userEmail.getEmail());
        assertThat(userDtoForJwt.getPassword()).isEqualTo(userEmail.getPassword());
        assertThat(userDtoForJwt.getPhoneNum()).isEqualTo(userEmail.getPhoneNum());

        //3. when email null, phoneNum present and password same
        //given
        LogInUserDto userDtoPhoneNum =  new LogInUserDto(null, "password", "phoneNum");
        User userPhoneNum = new User("email", "nickname",
        saltUtil.encodePassword(salt.getSalt(), "password"), "name", "phoneNum", 
        UserRole.ROLE_USER, salt);
        //when
        when(userRepository.findByPhoneNum("phoneNum")).thenReturn(
            Optional.ofNullable(userPhoneNum));
        //then
        LogInUserDto userDtoResponsePhoneNum = authService.logInUser(userDtoPhoneNum);
        assertThat(userDtoResponsePhoneNum.getPassword()).isEqualTo(userDtoPhoneNum.getPassword());
        assertThat(userDtoResponsePhoneNum.getPhoneNum()).isEqualTo(userDtoPhoneNum.getPhoneNum());

        //4. when userDto email and phoneNum do not match user
        //when
        when(userRepository.findByEmail("email")).thenReturn(
            Optional.ofNullable(null));
        when(userRepository.findByPhoneNum("phoneNum")).thenReturn(
            Optional.ofNullable(null));
        //then
        try {
            authService.logInUser(userDto);
        } catch (Exception e) {
            assertThat(e.getMessage()).isEqualTo("등록된 유저가 아닙니다.");
        }

    }

    @Test
    @DisplayName("change password user test")
    void changePasswordUser() throws Exception {

        //1. when user is not present
        //given
        ChangePasswordUserDto userDto =  new ChangePasswordUserDto("password", "phoneNum");
        //when
        when(userRepository.findByPhoneNum("phoneNum")).thenReturn(
            Optional.ofNullable(null));
        //then
        try {
            authService.changePasswordUser(userDto);
        } catch (Exception e) {
            assertThat(e.getMessage()).isEqualTo("등록되지 않은 유저입니다");
        }

        //2. when user is present but password invalid
        //given
        ChangePasswordUserDto userDtoPassword =  new ChangePasswordUserDto("password!@#", "phoneNum");
        User user = new User("email", "nickname", "password", "name", "phoneNum", 
        UserRole.ROLE_USER, new Salt(saltUtil.genSalt()));
        //when
        when(userRepository.findByPhoneNum("phoneNum")).thenReturn(
            Optional.ofNullable(user));
        //then
        try {
            authService.changePasswordUser(userDtoPassword);
        } catch (Exception e) {
            assertThat(e.getMessage()).isEqualTo("비밀번호 형식이 올바르지 않습니다");
        }
        
        //2. when user is present and password valid
        //given
        ChangePasswordUserDto userDtoValid =  new ChangePasswordUserDto("password", "phoneNum");
        //when
        ResponseDto responseDto = authService.changePasswordUser(userDtoValid);
        //then
        assertThat(responseDto.getCode()).isEqualTo("200");
        assertThat(responseDto.getMessage()).isEqualTo("비밀번호 변경이 완료되었습니다");

    }



}

