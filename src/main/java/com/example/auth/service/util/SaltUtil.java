package com.example.auth.service.util;

import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

@Service
public class SaltUtil {

    //BCrypt hash function을 사용. function 출력 전 password + salt값을 섞어줌.
    public String encodePassword(String salt, String password){
        return BCrypt.hashpw(password,salt);
    }

    public String genSalt(){
        return BCrypt.gensalt();
    }
}
