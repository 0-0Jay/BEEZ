package com.beez.beez.roles.web;

import com.beez.beez.roles.dto.ProjectPermissionResponse;
import com.beez.beez.roles.dto.ProjectRoleResponse;
import com.beez.beez.roles.service.RolesService;
import com.beez.beez.users.dto.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/project-auth")
@RequiredArgsConstructor
public class ProjectAuthController {

  private final RolesService rolesService;

  @GetMapping("/permissions/{projectId}")
  public ResponseEntity<List<ProjectPermissionResponse> > findPermissionsByProject(@PathVariable String projectId) {
    List<ProjectPermissionResponse> permissions = rolesService.findPermissionsByProject(projectId);
    return ResponseEntity.ok(permissions);
  }

  @GetMapping("/roles/{projectId}")
  public  ResponseEntity<List<ProjectRoleResponse>> findRolesByProject(@PathVariable String projectId) {
    List<ProjectRoleResponse> list = rolesService.findRolesByProject(projectId);
    return ResponseEntity.ok(list);
  }
}

