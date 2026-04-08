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
  
  //프로젝트 구성원 삭제
  void deleteProjectMember(String projectMemberId);
  
  //프로젝트 구성원 수정
  void updateProjectMember(String projectMemberId, ProjectMemberUpdateRequest dto);
  
  //역할 조회
  List<RolesResponse> findRoles();
  
  //사용자 + 그룹 검색 (합쳐서 반환)
  MemberSearchResponseDto searchMembers(String projectId, String keyword);
  
  //프로젝트 구성원 추가
  void insertProjectMember(ProjectMemberRequest dto);
  
}
