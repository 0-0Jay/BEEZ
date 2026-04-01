package com.beez.beez.project.service;

import com.beez.beez.project.dto.VersionCreateRequest;

public interface VersionService {
  //프로젝트 버전생성
  void insertVersion(VersionCreateRequest dto);
  
  //프로젝트 버전 수정
  void updateVersion(VersionCreateRequest dto);
  
  //버전 삭제
  void deleteVersion(String id, String projectId, String versionName);
  
}
