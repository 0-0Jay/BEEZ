package com.beez.beez.users.service;

import com.beez.beez.config.jwt.JwtProvider;
import com.beez.beez.permission.mapper.PermissionMapper;
import com.beez.beez.users.dto.CustomUserDetails;
import com.beez.beez.users.dto.PasswordUpdateRequest;
import com.beez.beez.users.mapper.UsersMapper;
import com.beez.beez.users.repository.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final UsersMapper usersMapper;
  private final PasswordEncoder passwordEncoder;
  private final JwtProvider jwtProvider;
  private final CustomUserDetailsService userDetailsService;

  // 로그인
  @Transactional(readOnly = true)
  public String login(String id, String password){
    // 사원번호로 사용자 조회
    Users user = usersMapper.findById(id).orElseThrow(() -> new RuntimeException("가입되지 않은 사원번호입니다."));

    // 비밀번호 확인
    if (!passwordEncoder.matches(password, user.getPassword())) {
      throw new RuntimeException("비밀번호가 일치하지 않습니다.");
    }

    // 계정 활성화 상태 확인
    if (!"H1".equals(user.getStatus())) {
      throw new DisabledException("비활성화된 계정입니다. 관리자에게 문의하세요.");
    }

    // 권한 넣기
    CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(id);

    // 로그인 성공 후 토큰 생성
    return jwtProvider.createToken(userDetails);
  }

  // 비밀번호 재설정
  @Transactional
  public void updatePassword(PasswordUpdateRequest dto){
    String encodedPw = passwordEncoder.encode(dto.getNewPassword());

    Map<String, Object> params = new HashMap<>();
    String result = "";
    params.put("password", encodedPw);
    params.put("token", dto.getToken());
    params.put("result", result);

    usersMapper.updatePassword(params);
    result = (String)params.get("result");

    // 성공
    if ("SUCCESS".equals(result)) {
      return;
    }

    // 그 외
    if("EXPIRED".equals(result))
      throw new RuntimeException("링크의 유효시간이 지났습니다. 다시 요청해 주세요.");
    else if ("INVALID".equals(result))
      throw new RuntimeException("잘못된 접근입니다.");
    else
      throw new RuntimeException("오류가 발생하였습니다.");
  }

}
