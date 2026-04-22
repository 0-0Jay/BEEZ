package com.beez.beez.project.dto;

import lombok.Data;

import java.util.Date;

@Data
public class VersionCreateRequest {
  private String id; //버전번호
  private String projectId; //프로젝트번호
  private String name; //버전명
  private String description; //버전설명
//  private Date createdOn; //생성일
  private Date startDate; //시작일
  private Date endDate; //마감일
  private String status; //버전상태값코드
  private String isShare; //버전 공유여부코드
  private Boolean isShareYn;   // 프론트에서 받는 boolean
  private String isDefault; // 프론트에서 체크 여부만 받고 DB엔 안 넣음
  private Boolean isDefaultYn;   // 프론트에서 받는 boolean
}
