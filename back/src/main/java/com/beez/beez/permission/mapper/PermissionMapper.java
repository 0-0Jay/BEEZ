package com.beez.beez.permission.mapper;

import com.beez.beez.permission.dto.PermissionListResponse;
import com.beez.beez.permission.repository.Permission;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PermissionMapper {

  // 권한 목록 조회
  List<PermissionListResponse> findAllPermissions();

  // 해당 역할의 권한 조회
  List<String> findPermIdsByRoleId(@Param("roleId") String id);

  // 역할별 권한 매핑 정보 일괄 등록
  void insertPermissionMapping(@Param("roleId") String id, @Param("perIds") List<String> perIds);

  // 역할 권한 매핑 삭제
  void deleteByRoleIdPermissionMapping(String roleId);


}
