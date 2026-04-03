package com.beez.beez.users.service.impl;

import com.beez.beez.users.dto.UserListResponse;
import com.beez.beez.users.dto.UserRegisterRequest;
import com.beez.beez.users.dto.UserSearchRequest;
import com.beez.beez.users.mapper.UsersMapper;
import com.beez.beez.users.repository.Users;
import com.beez.beez.users.service.MailService;
import com.beez.beez.users.service.UsersService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Transactional
@RequiredArgsConstructor
@Service
public class UsersServiceImpl implements UsersService {

  private final UsersMapper usersMapper;
  private final MailService mailService;
  private final PasswordEncoder passwordEncoder;

  // 초기 비밀번호 관련
  // 사용할 문자셋 (숫자 + 영문 대소문자)
  private static final String CHAR_SET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
  private static final SecureRandom sr = new SecureRandom();

  // 사용자 목록 조회
  @Override
  public List<UserListResponse> findAllUsers(UserSearchRequest search) {
    return usersMapper.findAllUsers(search);
  }

  // 사용자 등록 시 자동생성 될 정보 생성
  @Override
  public Map<String, String> getInitPw() {
    // 초기 비밀번호 생성
    StringBuilder sb = new StringBuilder(7);
    for(int i = 0; i < 7; i++) {
      sb.append(CHAR_SET.charAt(sr.nextInt(CHAR_SET.length())));
    }
    String initPw = sb.toString();

    Map<String, String> result = new HashMap<>();
    result.put("password", initPw);

    return result;
  }

  // 이메일 중복체크
  @Override
  public boolean checkEmailExists(String email) {
    return usersMapper.checkEmailExists(email) > 0;
  }

  // 사용자 등록
  @Override
  public void insertUser(UserRegisterRequest dto){
    // 이메일 중복 체크
    if (usersMapper.checkEmailExists(dto.getEmail()) > 0) {
      throw new RuntimeException("이미 사용 중인 이메일입니다: " + dto.getEmail());
    }

    // 암호화 전 비밀번호
    String rawPassword = dto.getPassword();

    // dto -> entity 변환
    Users user = dto.toEntity();

    // 비밀번호 암호화
    String encodedPw = passwordEncoder.encode(dto.getPassword());
    user.setPassword(encodedPw);

    usersMapper.insertUser(user);

    // 메일 발송
    mailService.sendWelcomeEmail(user.getEmail(), dto.getName(), rawPassword);
  }

}
