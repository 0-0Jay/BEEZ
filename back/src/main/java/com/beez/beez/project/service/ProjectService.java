package com.beez.beez.project.service;

import com.beez.beez.project.dto.ProjectCreateRequest;
import com.beez.beez.project.dto.ProjectFilterRequest;
import com.beez.beez.project.dto.ProjectListResponse;

import java.util.List;

public interface ProjectService {
  
  // 프로젝트 등록
  String insertProject(ProjectCreateRequest dto);
  
  // 프로젝트 목록 조회
  List<ProjectListResponse> selectProjectList(ProjectFilterRequest filter);
  
  //프로젝트 잠금보관
  void updateProjectLock(String id);
  
  //프로젝트 잠금보관 해제
  void updateUnProjectLock(String id);
  
  //프로젝트 삭제
  void updateProjectStatus(String id);
  
}
