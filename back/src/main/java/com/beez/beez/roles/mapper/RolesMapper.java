package com.beez.beez.roles.mapper;

import com.beez.beez.roles.repository.Roles;

public interface RolesMapper {

  // 새 역할 기본 정보 등록
  void insertRole(Roles roles);

  // 역할 이름 중복 체크
  int countByName(String name);
}
