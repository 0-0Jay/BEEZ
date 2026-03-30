package com.beez.beez.config.jwt;

import com.beez.beez.users.repository.Users;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;

@Component
public class JwtProvider {

  // 토큰을 만들고 검사하는 클래스
  //Jwts : jwt를 사용할 수 있도록 하는 라이브러리 안의 클래스 -> 토큰을 발급, 해석 및 검증(parser), 암호화 방식 등을 제공

  private final SecretKey key = Jwts.SIG.HS256.key().build(); // 암호화 키
  private final long tokenValidTime = 60 * 60 * 1000L; // 토큰 유효 시간 : 1시간

  // 토큰 발급
  public String createToken(Users user){
    List<String> roles = user.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();

    Date now = new Date();

    return Jwts.builder()
      .subject(user.getId()) // 사원번호를 식별
      .claim("roles", roles) // 어떤 권한을 가졌는지 토큰에 넣음
      .issuedAt(now) // 발급시간
      .expiration(new Date(now.getTime() + tokenValidTime)) // 만료 시간
      .signWith(key) // 비밀키로 전자 서명
      .compact(); // 문자열로 압축
  }

  // 프론트가 보낸 토큰으로 사번 확인
  public String getId(String token){
    return Jwts.parser()
      .verifyWith(key) // 키가 일치하는지 확인
      .build()
      .parseSignedClaims(token) // 토큰 안에 서명 검증
      .getPayload() // payload 꺼냄
      .getSubject(); // 그 안에 사번만 반환
  }

  // 토큰 유효성 검사
  public boolean validateToken(String token){
    try{
      // 토큰 검증 (토큰이 일치하지 않거나 만료 되면 예외 발생)
      Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
      return true;
    }catch (JwtException | IllegalArgumentException e){
      return false;
    }
  }

}
