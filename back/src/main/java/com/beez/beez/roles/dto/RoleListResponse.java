package com.beez.beez.roles.dto;

import lombok.Data;

@Data
public class RoleListResponse {

  private String id;
  private String name;
  private String perNames; // 권한 이름 리스트
  private int useCount; // 해당 역할을 사용하고 있는 수
}
