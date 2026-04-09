package com.beez.beez.file.service;

import com.beez.beez.file.dto.DocumentRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface DocumentService {
  //문서 등록
  void registerDocument(DocumentRequest.CreateRequest request, String userId, String projectId);
  
  //문서 목록 조회
  List<DocumentRequest.ListResponse> getDocumentList();
  
  //문서 상세 조회(문서 정보 + 최신파일 리스트)
  DocumentRequest.DetailResponse getDocumentDetail(String id);
  
  //문서 수정(버전 관리 로직 포함)
  void updateDocument(DocumentRequest.UpdateRequest request,
                      List<MultipartFile> newFiles,
                      String userId);
}
