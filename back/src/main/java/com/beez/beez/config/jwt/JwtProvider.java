package com.beez.beez.config.jwt;

import com.beez.beez.users.dto.CustomUserDetails;
import com.beez.beez.users.repository.Users;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.stream.Collectors;

@Component
public class JwtProvider {

  // 토큰을 만들고 검사하는 클래스
  //Jwts : jwt를 사용할 수 있도록 하는 라이브러리 안의 클래스 -> 토큰을 발급, 해석 및 검증(parser), 암호화 방식 등을 제공

  @Value("${jwt.secret}")
  private String secretKey; // 암호화 키
  private SecretKey key;

  // 의존성 주입 후 메서드 자동 실행
  @PostConstruct
  protected void init() {
    byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8); // 바이트 배열로 변환
    this.key = Keys.hmacShaKeyFor(keyBytes); // 비밀키 객체 생성
  }

  // 토큰 발급
  public String createToken(CustomUserDetails userDetails) {
    Users user = userDetails.getUser();

    System.out.println("로그인 유저 권한 체크: " + userDetails.getAuthorities());

    String authorities
      = userDetails.getAuthorities()
      .stream()
      .map(GrantedAuthority::getAuthority)
      .collect(Collectors.joining(","));

    Date now = new Date();

    // 토큰 유효 시간 : 1시간
    long tokenValidTime = 60 * 60 * 1000L;

    return Jwts.builder()
      .subject(user.getId()) // 사원번호를 식별
      .claim("name", user.getName())
      .claim("email", user.getEmail())
      //.claim("auth", authorities) // 어떤 권한을 가졌는지 토큰에 넣음
      .claim("role", user.getRole()) // 역할
      .issuedAt(now) // 발급시간
      .expiration(new Date(now.getTime() + tokenValidTime)) // 만료 시간
      .signWith(key) // 비밀키로 전자 서명
      .compact(); // 문자열로 압축
  }

  // 프론트가 보낸 토큰으로 사번 확인
  public String getId(String token){
    try {
      return Jwts.parser()
        .verifyWith(key) // 키가 일치하는지 확인
        .build()
        .parseSignedClaims(token) // 토큰 안에 서명 검증
        .getPayload() // payload 꺼냄
        .getSubject(); // 그 안에 사번만 반환
    } catch (Exception e) {
      return null;
    }
  }

  // 토큰 유효성 검사
  public boolean validateToken(String token){
    try{
      // 토큰 검증 (토큰이 일치하지 않거나 만료 되면 예외 발생)
      Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
      return true;
    }catch (ExpiredJwtException e) {
      System.out.println("토큰 만료");
    } catch (JwtException e) {
      System.out.println("토큰 변조 또는 오류");
    } catch (IllegalArgumentException e) {
      System.out.println("토큰 없음 또는 잘못된 값");
    }
    return false;
  }

}
