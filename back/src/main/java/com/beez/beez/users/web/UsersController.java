package com.beez.beez.users.web;

import com.beez.beez.users.dto.PasswordResetRequest;
import com.beez.beez.users.dto.UserListResponse;
import com.beez.beez.users.dto.UserRegisterRequest;
import com.beez.beez.users.dto.UserSearchRequest;
import com.beez.beez.users.service.PasswordService;
import com.beez.beez.users.service.UsersService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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

  // 이메일 중복체크
  @GetMapping("/create/check-email")
  public ResponseEntity<Boolean> checkEmailExists(@RequestParam("email") String email) {
    return  ResponseEntity.ok(usersService.checkEmailExists(email));
  }

  // 사용자 등록
  @PostMapping("/create")
  public ResponseEntity<String> insertUser(@RequestBody UserRegisterRequest dto) {
    try{
//      System.out.println("========================================");
//      System.out.println("등록 요청 도착!!! 데이터: " + dto);
//      System.out.println("========================================");
      usersService.insertUser(dto);
      return ResponseEntity.ok("사용자 등록이 완료되었습니다.");
    }catch (RuntimeException e) {
      // 중복 체크 등 에러 (사용자 입력 오류)
      return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    } catch (Exception e) {
      // 그 외 서버 내부 에러 (DB 연결 끊김 등)
      return ResponseEntity.internalServerError().body("서버 오류가 발생했습니다. 관리자에게 문의하세요.");
    }
  }
}
