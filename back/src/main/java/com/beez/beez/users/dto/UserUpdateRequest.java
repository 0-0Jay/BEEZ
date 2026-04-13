package com.beez.beez.users.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserUpdateRequest {

  private String name;
  private String email;
  private String status;

  @Size(min = 4, message = "비밀번호는 최소 4자 이상이어야 합니다.")
  private String newPassword;
}
