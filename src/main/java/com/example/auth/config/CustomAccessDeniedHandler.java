package com.example.auth.config;

import com.example.auth.dto.ResponseDto;
import com.example.auth.dao.entity.SecurityUser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AccessDeniedException e) throws IOException, ServletException {
        ObjectMapper objectMapper = new ObjectMapper();

        httpServletResponse.setStatus(200);
        httpServletResponse.setContentType("application/json;charset=utf-8");
        ResponseDto response = new ResponseDto("401","접근 가능 권한을 가지고 있지 않습니다.");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        SecurityUser user = (SecurityUser)authentication.getPrincipal();
        Collection<GrantedAuthority> authorities = user.getAuthorities();

        if(hasRole(authorities,UserRole.ROLE_NOT_PERMITTED.name())){
            new ResponseDto("401", "전화번호 인증을 받지 않았습니다.");
        }

        PrintWriter out = httpServletResponse.getWriter();
        String jsonResponse = objectMapper.writeValueAsString(response);
        out.print(jsonResponse);

    }

    private boolean hasRole(Collection<GrantedAuthority> authorites, String role){
        return authorites.contains(new SimpleGrantedAuthority(role));
    }

}