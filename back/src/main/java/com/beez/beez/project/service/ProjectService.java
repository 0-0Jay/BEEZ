package com.beez.beez.project.service;

import com.beez.beez.project.dto.*;

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
  
  //프로젝트 단건 조회
  ProjectInfoResponse findById(String id);
  
  //프로젝트 수정
  ProjectInfoResponse updateProject(String id, ProjectUpdateRequest dto);
  
  //프로젝트 구성원 조회
  ProjectMemberResponse findProjectMember(String projectId);
}
