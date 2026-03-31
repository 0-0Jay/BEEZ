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
  
  private String getCurrentUserId() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    return auth.getName();
  }
  
  @Override
  public String insertProject(ProjectCreateRequest dto) {
    String userId = getCurrentUserId();
    dto.setUserId(userId);
    projectMapper.insertProject(dto);
    
    String link = "/project/" + dto.getId();
    logsService.insertLogs(dto.getId(), "A1", "B0", "프로젝트 생성", link, userId);
    
    return dto.getId();
  }
  
  @Override
  public List<ProjectListResponse> selectProjectList(ProjectFilterRequest filter) {
    return projectMapper.selectProjectList(filter);
  }
  
  @Override
  public void updateProjectLock(String id) {
    String userId = getCurrentUserId();
    projectMapper.updateProjectLock(id);
    String link = "/project/list";
    logsService.insertLogs(id, "A0", "B0", "프로젝트 잠금보관", link, userId);
  }
  
  @Override
  public void updateUnProjectLock(String id) {
    String userId = getCurrentUserId();
    projectMapper.updateUnProjectLock(id);
    String link = "/project/list";
    logsService.insertLogs(id, "A0", "B0", "프로젝트 잠금보관 해제", link, userId);
  }
  
  @Override
  public void updateProjectStatus(String id) {
    String userId = getCurrentUserId();
    projectMapper.updateProjectStatus(id);
    String link = "/project/list";
    logsService.insertLogs(id, "A3", "B0", "프로젝트 삭제", link, userId);
  }
  
  @Override
  public ProjectInfoResponse findById(String id) {
    return projectMapper.findById(id);
  }
  
  @Override
  public ProjectInfoResponse updateProject(String id, ProjectUpdateRequest dto) {
    String userId = getCurrentUserId();
    dto.setId(id);
    
    String link = "/project/" + dto.getId();
    logsService.insertLogs(id, "A2", "B0", "프로젝트 수정", link, userId);
    
    projectMapper.updateProject(dto);
    return projectMapper.findById(id);
  }
  
}
