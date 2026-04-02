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
}
