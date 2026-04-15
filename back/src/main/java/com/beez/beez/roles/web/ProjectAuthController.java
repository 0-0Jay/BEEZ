package com.beez.beez.roles.web;

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
  public ResponseEntity<List<String> > findPermissionsByProject(@PathVariable String projectId, @AuthenticationPrincipal CustomUserDetails userDetails) {
    List<String> permissions = rolesService.findPermissionsByProject(userDetails.getUsername(), projectId);
    return ResponseEntity.ok(permissions);
  }
}

