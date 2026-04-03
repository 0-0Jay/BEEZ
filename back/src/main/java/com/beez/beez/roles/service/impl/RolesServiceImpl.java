package com.beez.beez.roles.service.impl;

import com.beez.beez.permission.mapper.PermissionMapper;
import com.beez.beez.roles.dto.RoleSaveRequest;
import com.beez.beez.roles.mapper.RolesMapper;
import com.beez.beez.roles.repository.Roles;
import com.beez.beez.roles.service.RolesService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class RolesServiceImpl implements RolesService {

  private final RolesMapper rolesMapper;
  private final PermissionMapper permissionMapper;

  // 새 역할 기본 정보 등록
  @Override
  public void insertRole(RoleSaveRequest dto) {
    // 역할 이름 중복 체크
    int count = rolesMapper.countByName(dto.getName());
    if (count > 0) {
      throw new RuntimeException("이미 존재하는 역할 이름입니다.: " + dto.getName());
    }

    Roles newRole
      = Roles.builder()
      .name(dto.getName())
      .isAssignee(dto.getIsAssignee())
      .build();

    rolesMapper.insertRole(newRole);

    if(dto.getPerIds() != null && !dto.getPerIds().isEmpty())
      permissionMapper.insertPermissionMapping(newRole.getId(), dto.getPerIds());

  }

}
