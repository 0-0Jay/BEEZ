package com.beez.beez.users.service;

import com.beez.beez.users.dto.UserListResponse;
import com.beez.beez.users.dto.UserRegisterRequest;
import com.beez.beez.users.dto.UserSearchRequest;

import java.util.List;
import java.util.Map;


public interface UsersService {

  // 사용자 목록 조회
  List<UserListResponse> findAllUsers(UserSearchRequest search);

  // 사용자 등록 시 자동생성 될 정보
  Map<String, String> getNextInfo();

  // 사용자 등록
  void insertUser(UserRegisterRequest userRegisterRequest);
}
