package com.beez.beez.project.dto;

import lombok.Data;

import java.util.Date;

@Data
public class ProjectListResponse {
  private String id;
  private String title;
  private String pmId;
  private String pmName;
  private Date endDate;
  private String isLock;
  
  // TODO: 일감 파트 팀원과 협의 후 추가 예정
//  private int totalIssues;      // 전체 일감 수
//  private int completedIssues;      // 완료된 일감 수
//  private int progress;         // 진행률 ( (completed / total) * 100 )

}
