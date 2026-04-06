package com.beez.beez.permission.web;

import com.beez.beez.permission.dto.PermissionListResponse;
import com.beez.beez.permission.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/permission")
public class PermissionController {

  private final PermissionService permissionService;

  @GetMapping("/list")
  public ResponseEntity<List<PermissionListResponse>> findAllPermissions(){
    return ResponseEntity.ok(permissionService.findAllPermissions());
  }

}
