package com.beez.beez.file.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileDetailRequest {
  private String originalName;
  private String storedName;  // UUID 사용 예정
  private String extension;
  private long fileSize;
  
  //260403 곽현우 추가
  private String fileId; //파일 묶음 ID
  private int version;   //버전
  private String rootId; //식별용 ID
}
