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

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

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
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @Spy
    private SaltUtil saltUtil;
    
    @InjectMocks
    public UserServiceImpl userService;

    @Test
    @DisplayName("find user info by refresh token test")
    void findUserInfoByRefreshToken() throws Exception {

        //1. when user is not present
        //given
        String refreshTokenInvalid = "dummy";
        //when
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(refreshTokenInvalid)).thenReturn("dummy");
        when(userRepository.findByPhoneNum("dummy")).thenReturn(
            Optional.ofNullable(null));
        //then
        try {
            userService.findUserInfoByRefreshToken(refreshTokenInvalid);
        } catch (Exception e) {
            assertThat(e.getMessage()).isEqualTo("등록되지 않은 유저입니다.");
        }

        //2. when user is present
        //given
        String refreshTokenValid = "valid";
        User user = new User("email", "nickname", "password", "name", "phoneNum", 
        UserRole.ROLE_USER, new Salt(saltUtil.genSalt()));
        //when
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(refreshTokenValid)).thenReturn("phoneNum");
        when(userRepository.findByPhoneNum("phoneNum")).thenReturn(
            Optional.ofNullable(user));
        UserInfoDto userDto = userService.findUserInfoByRefreshToken(refreshTokenValid);
        //then
        assertThat(userDto.getEmail()).isEqualTo(user.getEmail());
        assertThat(userDto.getPhoneNum()).isEqualTo(user.getPhoneNum());
        assertThat(userDto.getNickname()).isEqualTo(user.getNickname());
        assertThat(userDto.getName()).isEqualTo(user.getName());
        

    }



}

