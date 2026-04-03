package com.beez.beez.permission.service.impl;

import com.beez.beez.roles.mapper.RolesMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class PermissionServiceImpl {

  private final RolesMapper rolesMapper;
}
