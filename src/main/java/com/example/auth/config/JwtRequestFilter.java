package com.example.auth.config;

import com.example.auth.dto.*;
import com.example.auth.service.util.CookieUtil;
import com.example.auth.service.util.JwtUtil;
import com.example.auth.service.impl.UserDetailServiceImpl;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

/*
Spring Security는 세션 방식으로 사용자의 인증/허가를 주로 이루고 있다.
따라서 우리는 기존 방식을 Custom 하여 Token 방식으로 구성해야 할 것이다.

또한 스프링 Security는 사용자의 요청과 응답사이에 여러가지 기능을 수행하는 필터(Filter)를 두어 인증/허가 기능을 수행하고 있다.

간단하게 설명하자면,

 SecurityContextPersistenceFilter : SecurityContext 객체를 로딩하여 SecurityContextHolder에 저장하고 요청이 끝나면 삭제
LogoutFilter : 지정한 경로의 요청이 들어오면 사용자를 로그아웃시킴
UsernamePasswordAuthennticationFilter : 로그인 요청이 들어오면 아이디/비밀번호 기반의 인증을 수행한다.
FilterSecurityInterceptor : 인증에 성공한 사용자가 해당 리소스에 접근할 권한이 있는지를 검증

우리가 사용할 부분은 UsernamePasswordAuthenticationFilter 앞에 Custom Filter를 두어 세션이 존재하지 않아도 올바른 Jwt 값이 존재하면, SecurityContextHolder에 UserDetail 정보를 넣어 로그인 된 사용자로 인식 하도록 할 것이다.

구현은 다음과 같이 하였다.

Flow는 다음과 같다.
1. 로그인 한 사용자는 access token과 refresh token을 가지고 있다.
2. Access Token이 유효하면 AccessToken내 payload를 읽어 사용자와 관련있는 UserDetail을 생성
3. Access Token이 유효하지 않으면 Refresh Token값을 읽어드림.
4. Refresh Token을 읽어 Access Token을 사용자에게 재생성하고, 요청을 허가시킴.

발행된 AccessToken의 값은 무조건적으로 명백하다고 생각하여 요청을 허가시킴. But Access Token탈취의 위험이 존재하기 때문에 짧은 유효시간을 두어, Access Token이 탈취 당하더라도 만료되어 사용할 수 없도록 한다.
Refresh Token은 서버에서 그 값(Redis)을 저장함. Refresh Token을 사용할 상황이 오면 반드시 서버에서 그 유효성을 판별, 유효하지 않는 경우라면 요청을 거부. 혹은 사용자로부터 탈취 됐다라는 정보가 오면 그 Refrsh Token을 폐기할 수 있도록 설정.
먼저 프로그램을 만들고 블로그를 작성하다보니 redis에 관련된 부분을 제외해버렸다.
그래서 추가한다.

위 과정에서 Redis를 사용하는 이유는 다음과 같다.

Refresh Token을 서버에서 어디에다 저장할 것인가?

이미 정답을 제시하고 문제를 냈기 때문에 답하는데 김이 빠지긴 하지만 이유를 설명하자면 Refresh Token은 만료되어야 하기 때문이다.

*/

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private UserDetailServiceImpl userDetailService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CookieUtil cookieUtil;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {

        final Cookie jwtToken = cookieUtil.getCookie(httpServletRequest,JwtUtil.ACCESS_TOKEN_NAME);

        String userPhoneNum = null;
        String jwt = null;
        String refreshJwt = null;
        String refreshUserPhoneNum = null;

        try{
            if(jwtToken != null){
                jwt = jwtToken.getValue();
                userPhoneNum = jwtUtil.getPhoneNum(jwt);
            }
            if(userPhoneNum!=null){
                UserDetails userDetails = userDetailService.loadUserByUsername(userPhoneNum);

                if(jwtUtil.validateToken(jwt, userDetails)){
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
                    usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                }
            }
        }catch (ExpiredJwtException e){
            Cookie refreshToken = cookieUtil.getCookie(httpServletRequest,JwtUtil.REFRESH_TOKEN_NAME);
            if(refreshToken!=null){
                refreshJwt = refreshToken.getValue();
            }
        }catch(Exception e){

        }

        try{
            if(refreshJwt != null){
                ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
                refreshUserPhoneNum = valueOperations.get(refreshJwt);

                if(refreshUserPhoneNum.equals(jwtUtil.getPhoneNum(refreshJwt))){
                    UserDetails userDetails = userDetailService.loadUserByUsername(refreshUserPhoneNum);
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
                    usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);

                    LogInUserDto userDto = new LogInUserDto("dummyEmail", 
                    "dummyPW", refreshUserPhoneNum);
                    String newToken =jwtUtil.generateToken(userDto);

                    Cookie newAccessToken = cookieUtil.createCookie(JwtUtil.ACCESS_TOKEN_NAME,newToken);
                    httpServletResponse.addCookie(newAccessToken);
                    }
            }
        }catch(ExpiredJwtException e){

        }

        filterChain.doFilter(httpServletRequest,httpServletResponse);
    }
}