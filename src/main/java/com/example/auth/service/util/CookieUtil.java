package com.example.auth.service.util;

import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

@Component
public class CookieUtil {

    public Cookie createCookie(String cookieName, String value){
        Cookie cookie = new Cookie(cookieName,value);
        // document.cookie를 통한 쿠키 접속 방어
        cookie.setHttpOnly(true);

        cookie.setMaxAge((int)JwtUtil.TOKEN_VALIDATION_SECOND);
        cookie.setPath("/");
        return cookie;
    }

    public Cookie getCookie(HttpServletRequest req, String cookieName){
        final Cookie[] cookies = req.getCookies();
        if(cookies==null) return null;
        for(Cookie cookie : cookies){
            if(cookie.getName().equals(cookieName))
                return cookie;
        }
        return null;
    }


}
