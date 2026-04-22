package com.beez.beez.permission.service.impl;

import com.beez.beez.permission.dto.PermissionListResponse;
import com.beez.beez.permission.mapper.PermissionMapper;
import com.beez.beez.permission.repository.Permission;
import com.beez.beez.permission.service.PermissionService;
import com.beez.beez.roles.mapper.RolesMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {

  private final PermissionMapper permissionMapper;

  @Override
  public List<PermissionListResponse> findAllPermissions() {
    List<PermissionListResponse> permissions = permissionMapper.findAllPermissions();

    for (PermissionListResponse p : permissions) {
      String name = p.getName();
      int lastSpaceIndex = name.lastIndexOf(" ");

      if (lastSpaceIndex > 0) {
        p.setCategory(name.substring(0, lastSpaceIndex));
        p.setAction(name.substring(lastSpaceIndex + 1));
      } else {
        p.setCategory(name);
        p.setAction("");
      }
    }

    return permissions;
  }
}
