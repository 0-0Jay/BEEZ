package com.beez.beez.roles.web;

import com.beez.beez.roles.dto.RoleDetailResponse;
import com.beez.beez.roles.dto.RoleListResponse;
import com.beez.beez.roles.dto.RoleSaveRequest;
import com.beez.beez.roles.dto.RoleUpdateRequest;
import com.beez.beez.roles.service.RolesService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/roles")
public class RolesController {

  private final RolesService rolesService;

  // 역할 전체 조회
  @GetMapping("/list")
  public ResponseEntity<List<RoleListResponse>> findAllRoles() {
    List<RoleListResponse> list = rolesService.findAllRoles();
    return ResponseEntity.ok(list);
  }

  // 역할 상세 조회
  @GetMapping("/{id}")
  public ResponseEntity<RoleDetailResponse> findByRoleIdRole(@PathVariable String id) {
    RoleDetailResponse detail = rolesService.findByRoleIdRole(id);
    return ResponseEntity.ok(detail);
  }

  // 역할 등록
  @PostMapping("/add")
  public ResponseEntity<?> insertRole(@RequestBody RoleSaveRequest roleSaveRequest) {
    rolesService.insertRole(roleSaveRequest);
    return ResponseEntity.ok().body("역할 등록이 완료되었습니다.");
  }

  // 역할 수정
  @PutMapping("/{id}")
  public ResponseEntity<?> updateRole(@RequestBody RoleUpdateRequest roleUpdateRequest, @PathVariable String id) {
    rolesService.updateRoles(roleUpdateRequest, id);
    return ResponseEntity.ok().body("역할 수정이 완료되었습니다.");
  }

  // 역할 삭제
  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteRole(@PathVariable String id) {
    try {
      rolesService.deleteRoles(id);
      return ResponseEntity.ok().body("역할이 성공적으로 삭제되었습니다.");
    } catch (DataIntegrityViolationException e) {
      // 외래키 위반 시 발생하는 에러를 캐치!
      return ResponseEntity.status(HttpStatus.CONFLICT)
        .body("현재 사용 중인 역할은 삭제할 수 없습니다.");
    }
  }

  // 역할 복사
  @PostMapping("/copy")
  public ResponseEntity<?> copyRole(@RequestBody Map<String, String> params) {
    String originId = params.get("originId");
    String newName = params.get("newName");

    rolesService.copyRoles(originId, newName);
    return ResponseEntity.ok().body("역할 복사가 완료되었습니다.");
  }

}
