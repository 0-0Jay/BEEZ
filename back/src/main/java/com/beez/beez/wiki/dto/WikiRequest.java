package com.beez.beez.wiki.dto;

import lombok.*;

// DTO파일 - 데이터 담아 나르는 파일
// DTO - MAPPER&XML - service,impl - web/controller - 프론트 작업
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class WikiRequest {
  private String id;        //위키 번호
  private String projectId; //프로젝트 번호
  private String versionId; //현재 적용된 위키버전 번호
}


