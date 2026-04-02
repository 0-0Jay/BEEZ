package com.beez.beez.users.web;

import com.beez.beez.users.dto.MailSendDto;
import com.beez.beez.users.dto.PasswordResetRequest;
import com.beez.beez.users.dto.PasswordUpdateRequest;
import com.beez.beez.users.service.AuthService;
import com.beez.beez.users.service.MailService;
import com.beez.beez.users.service.PasswordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

  private final AuthService authService;
  private final PasswordService passwordService;
  private final MailService mailService;

  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody Map<String, String> loginData){

    System.out.println("=== 로그인 요청 진입 ===");
    System.out.println("요청 데이터: " + loginData);

    String id = loginData.get("id");
    String password = loginData.get("password");

    try{
      // 로그인 및 토큰 발급
      String token  = authService.login(id, password);

      // 로그인 성공 시 토큰을 JSON 형태로 반환
      Map<String, String> response = new HashMap<>();
      response.put("accessToken", token);

      return ResponseEntity.ok(response);
    }catch (DisabledException e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("비활성화된 계정입니다. 관리자에게 문의하세요.");
    } catch (BadCredentialsException e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("사원번호 또는 비밀번호가 일치하지 않습니다.");
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("로그인 중 오류가 발생했습니다.");
    }
  }

  // 비밀번호 재설정 메일 보내기
  @PostMapping("/reset-request")
  public ResponseEntity<?> requestReset(@RequestBody PasswordResetRequest dto) {
    MailSendDto mailDto = passwordService.sendResetPasswordEmail(dto);
    mailService.sendPasswordResetMail(mailDto.getEmail(), mailDto.getName(), mailDto.getToken());
    return ResponseEntity.ok().body("메일이 성공적으로 발송되었습니다.");
  }

  // 비밀번호 재설정
  @PostMapping("/reset-password")
  public ResponseEntity<?> resetPassword(@Valid @RequestBody PasswordUpdateRequest dto) {
    authService.updatePassword(dto);
    return ResponseEntity.ok().body("비밀번호 재설정을 완료하였습니다.");
  }

}
