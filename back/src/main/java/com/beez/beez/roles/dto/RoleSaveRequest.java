package com.beez.beez.roles.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleSaveRequest {

  private String name;
  private String isAssignee;
  private List<String> perIds; // 권한 리스트

}
