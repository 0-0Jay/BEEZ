package com.beez.beez.roles.service;

import com.beez.beez.roles.dto.RoleSaveRequest;

public interface RolesService {

  // 새 역할 기본 정보 등록
  void insertRole(RoleSaveRequest dto);

}
