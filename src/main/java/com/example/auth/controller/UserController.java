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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Cookie;

import java.time.Duration;


@RestController
@RequestMapping("/user")
public class UserController {

    private UserService userService;

    private CookieUtil cookieUtil;

    private JwtUtil jwtUtil;

    @Autowired
    public UserController(
        UserService userService,
        CookieUtil cookieUtil,
        JwtUtil jwtUtil) {
        this.userService = userService;
        this.cookieUtil = cookieUtil;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/userInfo")
    public ResponseEntity<UserInfoDto> findUserInfoByUserId(
        HttpServletRequest req
    ) {

        try {

            return ResponseEntity.ok()
            .body(
                userService.findUserInfoByUserId(
                    cookieUtil.getCookie(req, jwtUtil.REFRESH_TOKEN_NAME).getValue())
                );

        } catch (Exception e) {

            e.printStackTrace();
            return null;

        }

    }
  


}