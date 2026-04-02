package com.beez.beez.project.dto;

import lombok.Data;

@Data
public class VersionFilterRequest {
  private String id; //버전번호
  private String projectId; //프로젝트번호
  private String status; //버전상태값코드
}
