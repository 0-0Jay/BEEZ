package com.beez.beez.config.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  // 들어오는 모든 요청의 jwt를 검사하는 클래스

  private final JwtProvider jwtProvider;
  private final UserDetailsService userDetailsService;

  @Override
  protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {

    // 요청 헤더에서 토큰을 꺼냄
    String token = resolveToken(request);

    // 토큰 유효성 검사
    if(token != null && jwtProvider.validateToken(token)){
      // 토큰이 맞으면 사원번호 추출
      String id = jwtProvider.getId(token);

      // DB에서 사용자 정보를 가져와 인증됨을 보장해줌
      UserDetails userDetails = userDetailsService.loadUserByUsername(id);
      Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());

      // 시큐리티 세션(context)에 인증 정보를 저장(요청이 끝날 때까지만 유효)
      SecurityContextHolder.getContext().setAuthentication(auth);
    }

    filterChain.doFilter(request, response);
  }

  // 헤더에서 "Authorization: Bearer [토큰]" 형태의 값을 꺼내오는 메서드
  private String resolveToken(HttpServletRequest request) {
    String bearerToken = request.getHeader("Authorization");
    if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
      return bearerToken.substring(7);
    }
    return null;
  }

}
