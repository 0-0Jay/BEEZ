package com.beez.beez.users.dto;

import com.beez.beez.users.repository.Users;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRegisterRequest {
  private String id; // 사원번호
  private String email;
  private String password;
  private String name;
  private String role;

  public Users toEntity() {
    // 기본값 ROLE0003: 일반 사용자
    if (this.role == null || this.role.isEmpty()) {
      this.role = "ROLE0003";
    }

    return Users.builder()
      .id(this.id)
      .email(this.email)
      .password(this.password)
      .name(this.name)
      .status("H1") // 기본 활성화 상태
      .roles(List.of(this.role))
      .build();
  }

}
