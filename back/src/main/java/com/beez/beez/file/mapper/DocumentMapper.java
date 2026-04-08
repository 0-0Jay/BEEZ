package com.beez.beez.file.mapper;

import com.beez.beez.file.dto.DocumentRequest;
import com.beez.beez.file.dto.FileDetailRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DocumentMapper {
  
  // 등록 관련
    //파일 마스터 생성 - 파일들을 하나로 묶는 파일 바구니 생성 = 파일 테이블
    void insertFileMaster(@Param("id") String id, @Param("userId") String userId);
  
    //파일 상세 정보 저장 - 실제 파일 정보를 하나씩 집어 넣음 = 파일 디테일 테이블
    void insertFileDetail(FileDetailRequest fileDetail);
    
    //문서 정보 저장 - 파일 바구니 를 docs 테이블에 연결 = 문서테이블이랑 연결
    void insertDocument(@Param("doc") DocumentRequest.CreateRequest doc,
                       @Param("id") String id,
                       @Param("userId") String userId);
  
    
    
  // 수정 관련
    // 문서 기본 정보 수정 - 테이블의 제목이나 본문 내용을 최신 내용으로 업데이트 = 문서 테이블
    void updateDocument(DocumentRequest.UpdateRequest doc);
  
    // 기존 파일 논리 삭제 - 소트프 딜리트 처리 is_delete = y처리 하기 위함
    void deleteFileDetail(@Param("id") String targetFileDetailId);
  
    // 특정 파일의 최신 버전 정보 조회(버전업 위함) - root_id 미리 조회
    FileDetailRequest selectFileDetailById(String id);
  
    
    
  // 조회 관련
    // 목록 조회
  List<DocumentRequest.ListResponse> selectDocumentList();
  
    // 상세 조회
  DocumentRequest.DetailResponse selectDocumentDetail(String id);
  
    // 특정 문서에 걸린 최신 파일 리스트 조회
    List<com.beez.beez.file.dto.FileDetailResponse> selectLatestFilesByDocId(String id);
}
