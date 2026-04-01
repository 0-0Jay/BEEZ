package com.beez.beez.users.web;

import com.beez.beez.users.dto.PasswordResetRequest;
import com.beez.beez.users.dto.UserListResponse;
import com.beez.beez.users.dto.UserRegisterRequest;
import com.beez.beez.users.dto.UserSearchRequest;
import com.beez.beez.users.service.PasswordService;
import com.beez.beez.users.service.UsersService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UsersController {

  private final UsersService usersService;

  // 사용자 목록 조회
  @GetMapping("/list")
  public ResponseEntity<List<UserListResponse>> findAllUsers(@ModelAttribute UserSearchRequest search) {
    return ResponseEntity.ok(usersService.findAllUsers(search));
  }

  // 사용자 등록 시 자동생성 될 정보 가져오기
  @GetMapping("/getInitPw")
  public ResponseEntity<Map<String, String>> getInitPw() {
    return ResponseEntity.ok(usersService.getInitPw());
  }

  // 사용자 등록
  @PostMapping("/register")
  public ResponseEntity<String> insertUser(@RequestBody UserRegisterRequest dto) {
    try{
//      System.out.println("========================================");
//      System.out.println("등록 요청 도착!!! 데이터: " + dto);
//      System.out.println("========================================");
      usersService.insertUser(dto);
      return ResponseEntity.ok("사용자 등록이 완료되었습니다.");
    }catch(Exception e){
      return ResponseEntity.internalServerError().body("등록 실패: " + e.getMessage());
    }
  }
}
