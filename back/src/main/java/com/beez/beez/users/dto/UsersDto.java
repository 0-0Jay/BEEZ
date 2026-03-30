package com.beez.beez.users.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsersDto {
  private String id; // 사원번호
  private String email;
  private String password;
  private String name;
  private String role;
}
