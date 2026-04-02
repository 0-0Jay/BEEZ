package com.beez.beez.users.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
public class PasswordUpdateRequest {

  @NotBlank
  @Size(min = 4, message = "비밀번호는 최소 4자 이상이어야 합니다.") // 나중에 수정하기 -> 영문+숫자+특수문자 조합 8자 이상
  private String newPassword;

  @NotBlank
  private String token;
}
