package com.beez.beez.file.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

// 파일 수정 하기위함 - 수정버튼으로 버전관리 하기 위함
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileUpdateInfo {
  private String targetFileDetailId;
  private MultipartFile newFile;
  private boolean isDeleted;
}