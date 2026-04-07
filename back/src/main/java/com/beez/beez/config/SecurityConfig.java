package com.beez.beez.config;

import com.beez.beez.config.auth.DynamicAuthorizationManager;
import com.beez.beez.config.jwt.JwtAuthenticationFilter;
import com.beez.beez.config.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final JwtProvider jwtProvider;
  private final UserDetailsService userDetailsService;
  private final DynamicAuthorizationManager dynamicAuthorizationManager;

  @Bean
  public PasswordEncoder passwordEncoder(){
    return new BCryptPasswordEncoder();
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
    http
      .csrf(AbstractHttpConfigurer::disable)
      .httpBasic(AbstractHttpConfigurer::disable)
      .formLogin(AbstractHttpConfigurer::disable)
      // jwt 사용으로 세션 서버 저장 끄기(stateless)
      .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
      // url별 권한 제어
      .authorizeHttpRequests(auth -> auth
        // 누구나 접근 가능
        .requestMatchers("/api/auth/**").permitAll()
        // 관리자만
        .requestMatchers("/api/roles/**", "/api/users/**", "/api/permission/**", "/api/workflow/**").hasAuthority("ROLE0001")
        // 그 외 페이지
        .anyRequest().access(dynamicAuthorizationManager))
      // jwt 필터 추가
      .addFilterBefore(new JwtAuthenticationFilter(jwtProvider, userDetailsService), UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }
}
