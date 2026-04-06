package com.beez.beez.roles.dto;

import lombok.Data;

@Data
public class RoleListResponse {

  private String id;
  private String name;
  private String perNames; // 권한 이름 리스트
}
