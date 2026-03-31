package com.beez.beez.wiki.dto;

import lombok.Data;

import java.util.Date;

@Data
//위키 별 화면에 출력할 프로젝트 정보
public class WikiProjectRequest {
  private String id;
  private String identifier;
  private String title;
  private String description;
  private String isPublic;
  private String parentId;
  private Date startDate;
  private Date endDate;
  private String status;
  private String defaultVersionId;
  private String userId;
  private String userName; //20260327 곽현우 임시 추가
  
}
