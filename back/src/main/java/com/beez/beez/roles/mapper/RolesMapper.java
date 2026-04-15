package com.beez.beez.roles.mapper;

import com.beez.beez.roles.dto.RoleDetailResponse;
import com.beez.beez.roles.dto.RoleListResponse;
import com.beez.beez.roles.dto.RoleUpdateRequest;
import com.beez.beez.roles.repository.Roles;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface RolesMapper {

  // 역할 목록 조회
  List<RoleListResponse> findAllRoles();

  // 역할 상세 조회
  RoleDetailResponse findByRoleIdRole(String id);

  // 프로젝트별 역할 권한 조회
  List<String> findPermissionsByProject(@Param("userId") String userId, @Param("projectId") String projectId);

  int isProjectMember(@Param("userId") String userId, @Param("projectId") String projectId);

  // 새 역할 기본 정보 등록
  void insertRoles(Roles roles);

  // 역할 이름 중복 체크
  int countByName(String name);

  // 역할 이름 중복 체크 -> 같은 id 비교 제외
  int countByNameExcludingMe(String name, String id);

  // 역할 수정(이름, 권한 등)
  void updateByIdRoles(@Param("dto") RoleUpdateRequest dto, @Param("id") String id);

  // 역할 삭제
  int deleteByIdRoles(String id);

  // 역할 복사
  void copyRoles(Map<String, Object> params);

}
