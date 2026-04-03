package com.beez.beez.users.service;

import com.beez.beez.users.dto.UserListResponse;
import com.beez.beez.users.dto.UserRegisterRequest;
import com.beez.beez.users.dto.UserSearchRequest;

import java.util.List;
import java.util.Map;


public interface UsersService {

  // 사용자 목록 조회
  List<UserListResponse> findAllUsers(UserSearchRequest search);

  // 초기 비밀번호 생성
  Map<String, String> getInitPw();

  // 이메일 중복체크
  boolean checkEmailExists(String email);

  // 사용자 등록
  void insertUser(UserRegisterRequest userRegisterRequest);
}
