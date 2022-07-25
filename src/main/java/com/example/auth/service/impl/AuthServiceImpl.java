package com.example.auth.service.impl;

import com.example.auth.service.*;
import com.example.auth.service.util.*;
import com.example.auth.dto.*;
import com.example.auth.dao.entity.*;
import com.example.auth.dao.repository.*;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

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

@Service
public class AuthServiceImpl implements AuthService {

    private UserRepository userRepository;

    private SaltUtil saltUtil;

    @Autowired
    public AuthServiceImpl(
        UserRepository userRepository,
        SaltUtil saltUtil) {
        this.userRepository = userRepository;
        this.saltUtil = saltUtil;
    }

    @Transactional
    @Override
    public ResponseDto signUpUser(SignUpUserDto userDto) throws Exception {
        try{    
            // 이미 등록된 유저 여부 + 필드 유효성 검사
            if (userRepository.findByPhoneNum(userDto.getPhoneNum()).isPresent()) {
                throw new Exception("이미 등록된 유저입니다");
            } else if (!ValidateUtil.validatePhoneNum(userDto.getPhoneNum())) {
                throw new Exception("전화번호 형식이 올바르지 않습니다");
            } else if (!ValidateUtil.validateEmail(userDto.getEmail())) {
                throw new Exception("이메일 형식이 올바르지 않습니다");
            } else if (!ValidateUtil.validatePassword(userDto.getPassword())) {
                throw new Exception("비밀번호 형식이 올바르지 않습니다");
            } else if (!ValidateUtil.validateName(userDto.getName())) {
                throw new Exception("이름 형식이 올바르지 않습니다");
            }

            String salt = saltUtil.genSalt();
        
            User user = User.builder()
            .email(userDto.getEmail())
            .nickname(userDto.getNickname())
            .password(saltUtil.encodePassword(salt, userDto.getPassword()))
            .name(userDto.getName())
            .phoneNum(userDto.getPhoneNum())
            .salt(new Salt(salt))
            .build();

            userRepository.save(user);

            return new ResponseDto("200", "회원가입이 완료되었습니다");
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public LogInUserDto logInUser(LogInUserDto userDto) throws Exception {
        
        try {
            // select 두번 나가는 데 더 좋은 방법 생각해보기
            if (userRepository.findByEmail(userDto.getEmail()).isPresent()) {

                User user = userRepository.findByEmail(userDto.getEmail()).get();

                // 함수화 
                String userPassword = saltUtil.encodePassword(
                    user.getSalt().getSalt(), userDto.getPassword());
                if(!user.getPassword().equals(userPassword)) {
                    throw new Exception ("비밀번호가 틀립니다.");
                }

                /* Redis에 키를 리프레쉬 토큰, 밸류를 phoneNum으로 저장합니다.
                   이메일로 로그인 시 dto의 phoneNum이 비어있기 때문에 채워줍니다.*/
                LogInUserDto userDtoForJwt = new LogInUserDto(user.getEmail(),
                user.getPassword(), user.getPhoneNum());
                
                return userDtoForJwt;

            } else {
                User user = userRepository.findByPhoneNum(userDto.getPhoneNum())
                .orElseThrow(()-> new Exception("등록된 유저가 아닙니다."));
                
                String userPassword = saltUtil.encodePassword(
                    user.getSalt().getSalt(), userDto.getPassword());
                if(!user.getPassword().equals(userPassword)) {
                    throw new Exception ("비밀번호가 틀립니다.");
                }
                
                return userDto;
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }            
    }

    @Override
    @Transactional
    public ResponseDto changePasswordUser(ChangePasswordUserDto userDto) throws Exception {
        try {
            // 등록되지 않은 유저 여부 + 변경한 비밀번호 유효성 검사
            User user = userRepository.findByPhoneNum(userDto.getPhoneNum())
            .orElseThrow(()->new Exception("등록되지 않은 유저입니다"));
            
            if (!ValidateUtil.validatePassword(userDto.getPassword())) {
                throw new Exception("비밀번호 형식이 올바르지 않습니다");
            }
        
            user.changePassword(saltUtil.encodePassword(user.getSalt().getSalt(), userDto.getPassword()));
            
            userRepository.save(user);
        
            return new ResponseDto("200", "비밀번호 변경이 완료되었습니다");
            
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    };
    
}
