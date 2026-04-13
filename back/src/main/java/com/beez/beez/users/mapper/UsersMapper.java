package com.beez.beez.users.mapper;

import com.beez.beez.users.dto.*;
import com.beez.beez.users.repository.Users;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface UsersMapper {

  // 로그인
  Optional<Users> findById(String id);

  // 사용자 목록 조회
  List<UserListResponse> findAllUsers(UserSearchRequest search);

  // 이메일 중복체크
  int checkEmailExists(String email);

  // 사용자 등록
  void insertUser(Users user);

  // 사용자 정보 수정
  void updateUser(@Param("dto") UserUpdateRequest dto, String id);

  // 비밀번호 재설정 정보 조회
  UserListResponse findUserForPasswordReset(PasswordResetRequest dto);

  // 비밀번호 재설정 토큰 저장
  void insertUserTokens(UserTokenDto dto);

  // 비밀번호 재설정
  void updatePassword(Map<String, Object> map);

}
