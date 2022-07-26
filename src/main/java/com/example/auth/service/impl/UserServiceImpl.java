package com.example.auth.service.impl;

import com.example.auth.service.*;
import com.example.auth.service.util.*;
import com.example.auth.dto.*;
import com.example.auth.dao.entity.*;
import com.example.auth.dao.repository.*;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import javax.transaction.Transactional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import org.springframework.core.ParameterizedTypeReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.Set;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import javax.servlet.http.Cookie;

@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;

    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    public UserServiceImpl(
        UserRepository userRepository,
        RedisTemplate<String, String> redisTemplate) {
        this.userRepository = userRepository;
        this.redisTemplate = redisTemplate;
    }
    
    @Override
    public UserInfoDto findUserInfoByRefreshToken(String refreshToken) throws Exception {
        try{
            // refreshToken 정보로 userPhoneNum을 get
            ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
            String userPhoneNum = valueOperations.get(refreshToken);     
            // 이미 등록된 유저 여부
            User user = userRepository.findByPhoneNum(userPhoneNum)
            .orElseThrow(()-> new Exception("등록되지 않은 유저입니다."));
            // 스태틱 메서드를 사용해 리턴
            return UserInfoDto.from(user);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    };


}
