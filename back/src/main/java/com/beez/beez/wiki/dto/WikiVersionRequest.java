package com.beez.beez.wiki.dto;

import lombok.*;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WikiVersionRequest {
  private String versionId;
  private String content;
  private String userId;
  private String userName;
  private Timestamp createdOn;  //작성일시
  private String wikiId;       //연결된 위키번호
  private String description;  //버전설명 - 수정이유
  private String versionName; //버전이름
  private String links;        //관련링크
  private String wikiInfo;    //페이지 한줄 설명 글
}