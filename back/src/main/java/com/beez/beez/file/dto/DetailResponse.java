package com.beez.beez.file.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

//문서 상세
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DetailResponse {
  private String id;
  private String title;
  private String doctype;
  private String content;
  private String userName;
  private LocalDateTime createdOn;
  private List<FileDetailResponse> fileList; //영진님 파일을 리스트로 재사용
}