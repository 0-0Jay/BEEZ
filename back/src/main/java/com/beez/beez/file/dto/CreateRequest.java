package com.beez.beez.file.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

//새 문서 등록 화면
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateRequest {
  private String id;
  private String projectId;
  private String title;
  private String doctype;
  private String content;
  private String userId;
  private String fileId;
  private LocalDateTime createdOn;
  private List<MultipartFile> files; // 파일 업로드 위함
}