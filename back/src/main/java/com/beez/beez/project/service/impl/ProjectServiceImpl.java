package com.beez.beez.project.service.impl;

import com.beez.beez.aop.Loggable;
import com.beez.beez.logs.service.LogsService;
import com.beez.beez.project.dto.*;
import com.beez.beez.project.mapper.ProjectMapper;
import com.beez.beez.project.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@RequiredArgsConstructor
@Service
public class ProjectServiceImpl implements ProjectService {
  
  private final ProjectMapper projectMapper;
  
  //  세션에서 아이디 받아오기
  private String getCurrentUserId() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    return auth.getName();
  }
  
  // 프로젝트 생성
  @Loggable(
    logType = "A1",
    logCategory = "B0",
    content = "프로젝트 생성({id})",
    link = "/project/setting/{id}/info"
  )
  @Override
  public String insertProject(ProjectCreateRequest dto) {
    String userId = getCurrentUserId();
    dto.setUserId(userId);
    projectMapper.insertProject(dto);
    return dto.getId();
  }
  
  //프로젝트 식별자 중복체크
  @Override
  public Boolean checkIdentifier(String identifier, String projectId) {
    if (projectId == null) return projectMapper.countByIdentifier(identifier) > 0;
    return projectMapper.countByIdentifierExclude(identifier, projectId) > 0;
  }
  
  //프로젝트 이름 중복체크
  @Override
  public Boolean checkTitle(String title, String projectId) {
    if (projectId == null) return projectMapper.countByTitle(title) > 0;
    return projectMapper.countByTitleExclude(title, projectId) > 0;
  }
  
  
  // 프로젝트 목록 조회(필터링)
  @Override
  public List<ProjectListResponse> selectProjectList(ProjectFilterRequest filter) {
    return projectMapper.selectProjectList(filter);
  }
  
  // 프로젝트 잠금보관
  @Loggable(
    logType = "A0",
    logCategory = "B0",
    content = "프로젝트 잠금보관({id})",
    link = "/project/setting/{id}/info"
  )
  @Override
  public void updateProjectLock(String id) {
    projectMapper.updateProjectLock(id);
  }
  
  // 프로젝트 잠금보관 해제
  @Loggable(
    logType = "A0",
    logCategory = "B0",
    content = "프로젝트 잠금보관 해제({id})",
    link = "/project/setting/{id}/info"
  )
  @Override
  public void updateUnProjectLock(String id) {
    projectMapper.updateUnProjectLock(id);
  }
  
  // 프로젝트 삭제
  @Loggable(
    logType = "A3",
    logCategory = "B0",
    content = "프로젝트 삭제({id})",
    link = "/project/list"
  )
  @Override
  public void deleteProject(String id) {
    projectMapper.deleteProject(id);
  }
  
  // 프로젝트 단건 조회
  @Override
  public ProjectInfoResponse findById(String id) {
    return projectMapper.findById(id);
  }
  
  // 프로젝트 수정
  @Loggable(
    logType = "A2",
    logCategory = "B0",
    content = "프로젝트 수정({id})",
    link = "/project/setting/{id}/info"
  )
  @Override
  public ProjectInfoResponse updateProject(String id, ProjectUpdateRequest dto) {
    dto.setId(id);
    projectMapper.updateProject(dto);
    return projectMapper.findById(id);
  }
  
  // 프로젝트 구성원 조회
  @Override
  public ProjectMemberResponse findProjectMember(String projectId) {
    ProjectMemberResponse member = new ProjectMemberResponse();
    member.setProjectId(projectId);;
    projectMapper.findProjectMember(member);
    return member;
  }
  
  //프로젝트 구성원 추가
  @Loggable(
    logType = "A0",
    logCategory = "B0",
    content = "프로젝트 구성원 추가({projectId})",
    link = "/project/setting/{projectId}/members",
    idField = "projectId"
  )
  @Override
  public void insertProjectMember(ProjectMemberRequest dto) {
    projectMapper.insertProjectMember(dto);
  }
  
  //프로젝트 구성원 삭제
  @Loggable(
    logType = "A3",
    logCategory = "B0",
    content = "프로젝트 구성원 삭제({projectId})",
    link = "/project/setting/{projectId}/members",
    idField = "projectId"
  )
  @Override
  public void deleteProjectMember(String projectMemberId, String projectId) {
    projectMapper.deleteProjectMember(projectMemberId);
  }
  
  //프로젝트 구성원 수정
  @Loggable(
    logType = "A2",
    logCategory = "B0",
    content = "프로젝트 구성원 역할수정({projectId})",
    link = "/project/setting/{projectId}/members",
    idField = "projectId"
  )
  @Override
  public void updateProjectMember(String projectMemberId, ProjectMemberUpdateRequest dto) {
    dto.setMemberId(projectMemberId);
    projectMapper.updateProjectMember(dto);
  }
  
  //역할 조회
  @Override
  public List<RolesResponse> findRoles() {
    return projectMapper.findRoles();
  }
  
  // 사용자 + 그룹 검색 (합쳐서 반환)
  @Override
  public MemberSearchResponseDto searchMembers(String projectId, String keyword) {
    List<UserResponse> users = projectMapper.findUsers(projectId, keyword);
    List<GroupResponse> groups = projectMapper.findGroups(projectId, keyword);
    
    return MemberSearchResponseDto.builder()
      .users(users)
      .groups(groups)
      .build();
  }

  // 로드맵 목록 조회
  @Override
  public List<RoadmapListResponse> findRoadmapList(RoadmapFilterRequest filter) {
    return projectMapper.findRoadmapList(filter);
  }
  
}
