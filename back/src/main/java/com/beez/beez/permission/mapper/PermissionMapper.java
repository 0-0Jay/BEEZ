package com.beez.beez.permission.mapper;

import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PermissionMapper {

  // 역할별 권한 매핑 정보 일괄 등록
  void insertPermissionMapping(@Param("roleId") String id, @Param("perIds") List<String> perIds);
}
