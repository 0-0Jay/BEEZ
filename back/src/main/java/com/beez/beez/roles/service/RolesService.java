package com.beez.beez.roles.service;

import com.beez.beez.roles.dto.RoleListResponse;
import com.beez.beez.roles.dto.RoleSaveRequest;
import com.beez.beez.roles.dto.RoleUpdateRequest;

import java.util.List;

public interface RolesService {

  // 역할 목록 조회
  List<RoleListResponse> findAllRoles();

  // 새 역할 기본 정보 등록
  void insertRole(RoleSaveRequest dto);

  // 역할 수정(이름, 권한 등)
  void updateRoles(RoleUpdateRequest dto);

  // 역할 삭제
  void deleteRoles(String roleId);

  // 역할 복사
  void copyRoles(String originId, String newName);

}
