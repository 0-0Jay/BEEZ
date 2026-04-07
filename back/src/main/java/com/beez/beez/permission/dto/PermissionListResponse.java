package com.beez.beez.permission.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PermissionListResponse {

  private String id;
  private String name;
  private String method;
  private String url;

  // 가공용 필드
  private String category;  // "프로젝트"
  private String action;   // "조회
}
