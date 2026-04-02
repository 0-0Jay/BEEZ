package com.beez.beez.project.dto;

import lombok.Data;

import java.util.Date;

@Data
public class VersionListResponse {
  private String id; //버전번호
  private String projectId; //프로젝트번호
  private String name; //버전명
  private String description; //버전설명
  private Date startDate; //시작일
  private Date endDate; //마감일
  private String status; //버전상태값코드
  private String isShare; //버전 공유여부코드
  private Boolean isDefault; // 기본버전여부
}
