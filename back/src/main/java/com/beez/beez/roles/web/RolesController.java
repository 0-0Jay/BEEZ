package com.beez.beez.roles.web;

import com.beez.beez.roles.dto.RoleListResponse;
import com.beez.beez.roles.dto.RoleSaveRequest;
import com.beez.beez.roles.service.RolesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/roles")
public class RolesController {

  private final RolesService rolesService;

  @GetMapping("/list")
  public ResponseEntity<List<RoleListResponse>> findAllRoles() {
    List<RoleListResponse> list = rolesService.findAllRoles();
    return ResponseEntity.ok(list);
  }

  @PostMapping("/add")
  public ResponseEntity<?> insertRole(@RequestBody RoleSaveRequest roleSaveRequest) {
    rolesService.insertRole(roleSaveRequest);
    return ResponseEntity.ok().body("역할 등록이 완료되었습니다.");
  }

  @DeleteMapping("/{roleId}")
  public ResponseEntity<?> deleteRole(@PathVariable("roleId") String roleId) {
    rolesService.deleteRoles(roleId);
    return ResponseEntity.ok().body("역할이 성공적으로 삭제되었습니다.");
  }

  @PostMapping("/copy")
  public ResponseEntity<?> copyRole(@RequestBody Map<String, String> params) {
    String originId = params.get("originId");
    String newName = params.get("newName");

    rolesService.copyRoles(originId, newName);
    return ResponseEntity.ok().body("역할 복사가 완료되었습니다.");
  }

}
