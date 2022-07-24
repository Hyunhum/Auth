package com.example.auth.service.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

import com.example.auth.dto.LogInUserDto;

/// 토큰 검사, 유효기간 체크, 발행, 재발행 하는 component
@Component
public class JwtUtil {

    public final static long TOKEN_VALIDATION_SECOND = 30 * 60 * 1000L;   // 30분
    public final static long REFRESH_TOKEN_VALIDATION_SECOND = 60 * 60 * 24 * 365 * 1000L;   // 30일

    final static public String ACCESS_TOKEN_NAME = "accessToken";
    final static public String REFRESH_TOKEN_NAME = "refreshToken";

    @Value("${spring.jwt.secret}")
    private String SECRET_KEY;

    private Key getSigningKey(String secretKey) {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
    // 토큰의 유효성을 검사한 후 토큰의 payload값 추출
    public Claims extractAllClaims(String token) throws ExpiredJwtException {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey(SECRET_KEY))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    // 추출한 payload로부터 phoneNum을 get
    public String getPhoneNum(String token) {
        return extractAllClaims(token).get("phoneNum", String.class);
    }
    // token이 expired됐는지 여부
    public Boolean isTokenExpired(String token) {
        final Date expiration = extractAllClaims(token).getExpiration();
        return expiration.before(new Date());
    }
    // 액세스 토큰 생성
    public String generateToken(LogInUserDto userDto) {
        return doGenerateToken(userDto.getPhoneNum(), TOKEN_VALIDATION_SECOND);
    }
    // 리프레쉬 토큰 생성
    public String generateRefreshToken(LogInUserDto userDto) {
        return doGenerateToken(userDto.getPhoneNum(), REFRESH_TOKEN_VALIDATION_SECOND);
    }
    // 토큰을 생성, 페이로드에는 phoneNum을 넣어줌
    public String doGenerateToken(String phoneNum, long expireTime) {

        Claims claims = Jwts.claims();
        claims.put("phoneNum", phoneNum);

        String jwt = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expireTime))
                .signWith(getSigningKey(SECRET_KEY), SignatureAlgorithm.HS256)
                .compact();

        return jwt;
    }
    // 토큰 검증
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String phoneNum = getPhoneNum(token);

        return (phoneNum.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

}