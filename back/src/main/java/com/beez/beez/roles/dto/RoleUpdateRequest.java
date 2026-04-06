package com.beez.beez.roles.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class RoleUpdateRequest {

  private String id;
  private String name;
  private String isAssignee;
  private List<String> perIds; // 권한 리스트

}
