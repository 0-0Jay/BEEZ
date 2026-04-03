package com.beez.beez.roles.web;

import com.beez.beez.roles.dto.RoleSaveRequest;
import com.beez.beez.roles.service.RolesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/roles")
public class RolesController {

  private final RolesService rolesService;

  @PostMapping("/register")
  public ResponseEntity<?> insertRole(@RequestBody RoleSaveRequest roleSaveRequest) {
    rolesService.insertRole(roleSaveRequest);
    return ResponseEntity.ok().body("역할 등록이 완료되었습니다.");
  }

}
