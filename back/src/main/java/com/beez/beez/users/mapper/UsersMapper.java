package com.beez.beez.users.mapper;

import com.beez.beez.users.dto.UserListResponse;
import com.beez.beez.users.dto.UserSearchRequest;
import com.beez.beez.users.repository.Users;

import java.util.List;
import java.util.Optional;

public interface UsersMapper {

  // 로그인
  Optional<Users> findById(String id);

  // 사용자 목록 조회
  List<UserListResponse> findAllUsers(UserSearchRequest search);

  // 사용자 등록
  void insertUser(Users user);
}
