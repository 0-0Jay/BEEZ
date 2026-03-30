package com.beez.beez.users.service;

import com.beez.beez.config.jwt.JwtProvider;
import com.beez.beez.users.mapper.UsersMapper;
import com.beez.beez.users.repository.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final UsersMapper usersMapper;
  private final PasswordEncoder passwordEncoder;
  private final JwtProvider jwtProvider;

  @Transactional(readOnly = true)
  public String login(String id, String password){
    // 사원번호로 사용자 조회
    Users user = usersMapper.findById(id).orElseThrow(() -> new RuntimeException("가입되지 않은 사원번호입니다."));

    // 비밀번호 확인
    if (!passwordEncoder.matches(password, user.getPassword())) {
      throw new RuntimeException("비밀번호가 일치하지 않습니다.");
    }

    // 로그인 성공 후 토큰 생성
    return jwtProvider.createToken(user);
  }

}
