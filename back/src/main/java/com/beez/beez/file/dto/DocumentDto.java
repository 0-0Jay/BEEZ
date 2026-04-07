package com.beez.beez.file.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

//260407 곽현우 생성

public class DocumentDto {
  //문서 목록 화면
  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class ListResponse {
    private String id;
    private String title;
    private String doctype;
    private String userName;
    private LocalDateTime createdOn;
    private String isMarked;
  }
  
  //새 문서 등록 화면
  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class CreateRequest {
    private String title;
    private String doctype;
    private String content;
    private List<MultipartFile> files; // 파일 업로드 위함
  }
  
  //문서 상세
  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class DetailResponse {
    private String id;
    private String title;
    private String doctype;
    private String content;
    private String userName;
    private LocalDateTime createdOn;
    private List<FileDetailResponse> fileList; //영진님 파일을 리스트로 재사용
  }
  
  //문서 수정
  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class UpdateRequest {
    private String id;
    private String title;
    private String doctype;
    private String content;
    private String changeReason;  //수정사유
    private List<FileUpdateInfo> fileUpdates;
    
  }
  
  // 파일 수정 하기위함 - 수정버튼으로 버전관리 하기 위함
  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class FileUpdateInfo {
    private String targetFileDetailId;
    private MultipartFile newFile;
    private boolean isDeleted;
  }
  
  
} //최상급 dto end
