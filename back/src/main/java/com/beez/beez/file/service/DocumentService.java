package com.beez.beez.file.service;

import com.beez.beez.file.dto.CreateRequest;
import com.beez.beez.file.dto.DetailResponse;
import com.beez.beez.file.dto.ListResponse;
import com.beez.beez.file.dto.UpdateRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface DocumentService {
  //문서 등록
  void registerDocument(CreateRequest request);
  
  //문서 목록 조회
  List<ListResponse> getDocumentList(String projectId);
  
  //문서 상세 조회(문서 정보 + 최신파일 리스트)
  DetailResponse getDocumentDetail(String id);
  
  //문서 수정(버전 관리 로직 포함)
  void updateDocument(UpdateRequest request,
                      List<MultipartFile> newFiles,
                      String userId);
}
