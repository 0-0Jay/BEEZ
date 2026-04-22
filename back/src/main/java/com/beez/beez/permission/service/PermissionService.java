package com.beez.beez.permission.service;

import com.beez.beez.permission.dto.PermissionListResponse;

import java.util.List;

public interface PermissionService {

  // 권한 목록 조회
  List<PermissionListResponse> findAllPermissions();

}
