package com.beez.beez.users.service;

import com.beez.beez.users.dto.UserListResponse;
import com.beez.beez.users.dto.UserSearchRequest;

import java.util.List;


public interface UsersService {

  // 사용자 목록 조회
  List<UserListResponse> findAllUsers(UserSearchRequest search);

}
