package com.beez.beez.project.service.impl;

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
  private final LogsService logsService;
  private ProjectService projectService;
  
  //  세션에서 아이디 받아오기
  private String getCurrentUserId() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    return auth.getName();
  }
  
  // 프로젝트 생성
  @Override
  public String insertProject(ProjectCreateRequest dto) {
    String userId = getCurrentUserId();
    dto.setUserId(userId);
    projectMapper.insertProject(dto);
    
    String link = "/project/" + dto.getId();
    String content = String.format("프로젝트 생성 (프로젝트명 : %s)", dto.getTitle());
    logsService.insertLogs(dto.getId(), "A1", "B0", content, link, userId);
    
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
  @Override
  public void updateProjectLock(String id) {
    String userId = getCurrentUserId();
    ProjectInfoResponse project = projectMapper.findById(id);
    projectMapper.updateProjectLock(id);
    
    String link = "/project/list";
    String content = String.format("프로젝트 잠금보관 (프로젝트명 : %s)", project.getTitle());
    logsService.insertLogs(id, "A0", "B0", content, link, userId);
  }
  
  // 프로젝트 잠금보관 해제
  @Override
  public void updateUnProjectLock(String id) {
    String userId = getCurrentUserId();
    ProjectInfoResponse project = projectMapper.findById(id);
    projectMapper.updateUnProjectLock(id);
    
    String link = "/project/list";
    String content = String.format("프로젝트 잠금보관 해제 (프로젝트명 : %s)", project.getTitle());
    logsService.insertLogs(id, "A0", "B0", content, link, userId);
  }
  
  // 프로젝트 삭제
  @Override
  public void updateProjectStatus(String id) {
    String userId = getCurrentUserId();
    ProjectInfoResponse project = projectMapper.findById(id);
    projectMapper.updateProjectStatus(id);
    
    String link = "/project/list";
    String content = String.format("프로젝트 삭제 (프로젝트명 : %s)", project.getTitle());
    logsService.insertLogs(id, "A3", "B0", content, link, userId);
  }
  
  // 프로젝트 단건 조회
  @Override
  public ProjectInfoResponse findById(String id) {
    return projectMapper.findById(id);
  }
  
  // 프로젝트 수정
  @Override
  public ProjectInfoResponse updateProject(String id, ProjectUpdateRequest dto) {
    String userId = getCurrentUserId();
    dto.setId(id);
    ProjectInfoResponse project = projectMapper.findById(id);
    
    String link = "/project/" + dto.getId();
    String content = String.format("프로젝트 수정 (프로젝트명 : %s)", project.getTitle());
    logsService.insertLogs(id, "A2", "B0", content, link, userId);
    
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
  
  //프로젝트 구성원 삭제
  @Override
  public void deleteProjectMember(String projectMemberId) {
    projectMapper.deleteProjectMember(projectMemberId);
  }
  
  //프로젝트 구성원 수정
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
  
  //프로젝트 구성원 추가
  @Override
  public void insertProjectMember(ProjectMemberRequest dto) {
    projectMapper.insertProjectMember(dto);
  }
  
  
}
