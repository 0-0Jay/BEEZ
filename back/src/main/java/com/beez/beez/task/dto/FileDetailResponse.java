package com.beez.beez.task.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileDetailResponse {
  private String id;
  private String fileId;
  private String originalName;
  private String storedName;
  private String extension;
  private long fileSize;
}
