package com.example.auth.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.annotation.PostConstruct;
import java.security.Security;

import com.example.auth.service.impl.UserDetailServiceImpl;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailServiceImpl userDetailServiceImpl;

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @Autowired
    private CustomAuthenticationEntryPoint customAuthEntryPoint;

    @Autowired
    private CustomAccessDeniedHandler customAccessDeniedHandler;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
      http.csrf().disable() // JWT를 쓰는 큰 이유 중 하나가 CSRF 막는 것
              .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 세션 기본 사용 안하고, 생성도 하지 않는다
              .and()
              .httpBasic()
              .authenticationEntryPoint(customAuthEntryPoint)
              .and()
              .exceptionHandling().accessDeniedHandler(customAccessDeniedHandler)
              .and()
              .authorizeRequests()
              .antMatchers("/auth/**", "/h2/**").permitAll()
              .anyRequest().authenticated();
      
      http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

  }

  @Bean
  @Override
  public AuthenticationManager authenticationManagerBean() throws Exception {
      return super.authenticationManagerBean();
  }



}