package com.beez.beez.file.mapper;

import com.beez.beez.file.dto.DocumentRequest;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DocumentMapper {
  
  // 등록 관련
    //파일 마스터 생성
  
    //파일 상세 정보 저장
  
    //문서 정보 저장
  
  
  // 수정 관련
    // 문서 기본 정보 수정
  
    // 기존 파일 논리 삭제
  
    // 특정 파일의 최신 버전 정보 조회(버전업 위함)
  
  
  // 조회 관련
    // 목록 조회
  List<DocumentRequest.ListResponse> selectDocumentList();
  
    // 상세 조회
  DocumentRequest.DetailResponse selectDocumentDetail(String id);
  
    // 특정 문서에 걸린 최신 파일 리스트 조회? - 이건 왜 필요한거지
    //List<com.beez.beez.file.dto.FileDetailResponse> selectLatestFilesByDocId(String id);
}
