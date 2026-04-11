package com.beez.beez.users.web;

import com.beez.beez.users.dto.UserListResponse;
import com.beez.beez.users.dto.UserRegisterRequest;
import com.beez.beez.users.dto.UserSearchRequest;
import com.beez.beez.users.dto.UserUpdateRequest;
import com.beez.beez.users.service.UsersService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

  private final UsersService usersService;

  // 사용자 정보 수정
  @PutMapping("/{id}")
  public ResponseEntity<String> updateUser(@PathVariable String id, @Valid @RequestBody UserUpdateRequest dto) {
    try {
      usersService.updateUser(dto, id);
      return ResponseEntity.ok("사용자 수정이 완료되었습니다.");
    } catch (RuntimeException e) {
      if (e.getMessage().equals("수정 권한이 없습니다.")) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
      }
      return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    } catch (Exception e) {
      return ResponseEntity.internalServerError().body("서버 오류가 발생했습니다.");
    }
  }
}
