package com.example.auth.controller;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Cookie;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import com.example.auth.service.*;
import com.example.auth.service.util.*;
import com.example.auth.dto.*;

import java.time.Duration;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private AuthService authService;

    private CookieUtil cookieUtil;

    private JwtUtil jwtUtil;

    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    public AuthController(
        AuthService authService,
        CookieUtil cookieUtil,
        JwtUtil jwtUtil,
        RedisTemplate<String, String> redisTemplate) {
        this.authService = authService;
        this.cookieUtil = cookieUtil;
        this.jwtUtil = jwtUtil;
        this.redisTemplate = redisTemplate;
    }

    @PostMapping("/signup")
    public ResponseEntity<ResponseDto> signUpUser(
        @RequestBody SignUpUserDto signUpUserDto){

        try {

            return ResponseEntity.ok()
            .body(
                authService.signUpUser(signUpUserDto)
                );

        } catch (Exception e) {

            e.printStackTrace();
            return null;

        }

    }

    @GetMapping("/login")
    public ResponseEntity<ResponseDto> logInUser(
        @RequestBody LogInUserDto userDto,
        HttpServletRequest req, 
        HttpServletResponse res){

        try {

            final LogInUserDto userDtoResponse = authService.logInUser(userDto);
            final String token = jwtUtil.generateToken(userDtoResponse);
            final String refreshJwt = jwtUtil.generateRefreshToken(userDtoResponse);
            
            Cookie accessToken = cookieUtil.createCookie(JwtUtil.ACCESS_TOKEN_NAME, token);
            Cookie refreshToken = cookieUtil.createCookie(JwtUtil.REFRESH_TOKEN_NAME, refreshJwt);
            
            ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
            valueOperations.set(refreshJwt, userDtoResponse.getPhoneNum(), 
            Duration.ofSeconds(JwtUtil.REFRESH_TOKEN_VALIDATION_SECOND));
            
            res.addCookie(accessToken);
            res.addCookie(refreshToken);

            return ResponseEntity.ok()
            .body(
                new ResponseDto("200", "로그인이 완료되었습니다")
                );

        } catch (Exception e) {

            e.printStackTrace();
            return null;

        }

    }

    @PostMapping("/changePassword")
    public ResponseEntity<ResponseDto> changePasswordUser(
        @RequestBody ChangePasswordUserDto changePasswordUserDto){

        try {

            return ResponseEntity.ok()
            .body(
                authService.changePasswordUser(changePasswordUserDto)
                );

        } catch (Exception e) {

            e.printStackTrace();
            return null;

        }

    }


}
