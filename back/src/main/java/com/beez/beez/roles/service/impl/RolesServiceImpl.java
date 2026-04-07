package com.beez.beez.roles.service.impl;

import com.beez.beez.permission.mapper.PermissionMapper;
import com.beez.beez.roles.dto.RoleDetailResponse;
import com.beez.beez.roles.dto.RoleListResponse;
import com.beez.beez.roles.dto.RoleSaveRequest;
import com.beez.beez.roles.dto.RoleUpdateRequest;
import com.beez.beez.roles.mapper.RolesMapper;
import com.beez.beez.roles.repository.Roles;
import com.beez.beez.roles.service.RolesService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
@RequiredArgsConstructor
public class RolesServiceImpl implements RolesService {

  private final RolesMapper rolesMapper;
  private final PermissionMapper permissionMapper;

  // 역할 전체 조회
  @Override
  public List<RoleListResponse> findAllRoles() {
    return rolesMapper.findAllRoles();
  }

  // 역할 상세 조회
  @Override
  public RoleDetailResponse findByRoleIdRole(String id) {
    return rolesMapper.findByRoleIdRole(id);
  }

  // 새 역할 기본 정보 등록
  @Override
  public void insertRole(RoleSaveRequest dto) {
    // 역할 이름 중복 체크
    int count = rolesMapper.countByName(dto.getName());
    if (count > 0) {
      throw new RuntimeException("이미 존재하는 역할 이름입니다.");
    }

    Roles newRole
      = Roles.builder()
      .name(dto.getName())
      .isAssignee(dto.getIsAssignee())
      .build();

    rolesMapper.insertRoles(newRole);

    if(dto.getPerIds() != null && !dto.getPerIds().isEmpty())
      permissionMapper.insertPermissionMapping(newRole.getId(), dto.getPerIds());

  }

  // 역할 수정(이름, 권한 등)
  @Override
  public void updateRoles(RoleUpdateRequest dto, String id) {
    // 내 ID를 제외하고 이름 중복 체크
    int count = rolesMapper.countByNameExcludingMe(dto.getName(), id);
    if (count > 0) {
      throw new RuntimeException("다른 역할에서 이미 사용 중인 이름입니다.");
    }

    // 역할 정보 수정
    rolesMapper.updateByIdRoles(dto, id);

    // 기존 권한 매핑 데이터 전체 삭제
    permissionMapper.deleteByRoleIdPermissionMapping(id);

    // 새로 선택된 권한들이 있다면 다시 넣기
    if (dto.getPerIds() != null && !dto.getPerIds().isEmpty()) {
      permissionMapper.insertPermissionMapping(id, dto.getPerIds());
    }
  }

  // 역할 삭제
  @Override
  public void deleteRoles(String id) {
    // 권한 매핑 삭제(자식)
    permissionMapper.deleteByRoleIdPermissionMapping(id);

    // 역할 삭제(부모)
    int result = rolesMapper.deleteByIdRoles(id);

    if(result == 0)
      throw new RuntimeException("역할 삭제를 실패하였습니다.");
  }

  // 역할 복사
  @Override
  public void copyRoles(String originId, String newName) {
    // 역할 이름 중복 체크
    int count = rolesMapper.countByName(newName);
    if (count > 0) {
      throw new RuntimeException("이미 존재하는 역할 이름입니다.: " + newName);
    }

    Map<String, Object> params = new HashMap<>();
    params.put("originId", originId);
    params.put("newName", newName);

    rolesMapper.copyRoles(params);
  }

}
