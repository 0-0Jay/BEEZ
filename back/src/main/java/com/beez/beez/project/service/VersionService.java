package com.beez.beez.project.service;

import com.beez.beez.project.dto.VersionCreateRequest;
import com.beez.beez.project.dto.VersionFilterRequest;
import com.beez.beez.project.dto.VersionListResponse;
import com.beez.beez.task.dto.CommonCodeResponse;

import java.util.List;

public interface VersionService {
  //프로젝트 버전생성
  void insertVersion(VersionCreateRequest dto);
  
  //프로젝트 버전 수정
  void updateVersion(VersionCreateRequest dto);
  
  //버전 삭제
  void deleteVersion(String id, String projectId, String versionName);
  
  //버전 목록조회(필터링)
  List<VersionListResponse> selectVersionList(VersionFilterRequest filter);
  
  // 공통코드 목록
  List<CommonCodeResponse> findCommonCodeList();
}
