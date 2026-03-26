package com.beez.beez.project.dto;

import lombok.Data;

import java.util.Date;

@Data
public class ProjectListResponse {
  private String id;
  private String title;
  private String userId;
  private Date endDate;
  
  // TODO: 일감 파트 팀원과 협의 후 추가 예정
  // private int issueCount;      // 일감 수
  // private int progressRate;    // 프로젝트 진행률
}
