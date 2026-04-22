package com.beez.beez.file.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

//문서 수정
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateRequest {
  private String projectId;
  private String id;
  private String title;
  private String doctype;
  private String userId;
  private String content;
  private String changeReason;  //수정사유
  private List<FileUpdateInfo> fileUpdates;
  
}