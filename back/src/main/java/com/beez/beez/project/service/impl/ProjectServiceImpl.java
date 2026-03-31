package com.beez.beez.project.service.impl;

import com.beez.beez.logs.service.LogsService;
import com.beez.beez.project.dto.*;
import com.beez.beez.project.mapper.ProjectMapper;
import com.beez.beez.project.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@RequiredArgsConstructor
@Service
public class ProjectServiceImpl implements ProjectService {
  
  private final ProjectMapper projectMapper;
  private final LogsService logsService;
  
  @Override
  public String insertProject(ProjectCreateRequest dto) {
    dto.setUserId("20210001");
    projectMapper.insertProject(dto);
    
    String link = "/project/" + dto.getId();
    logsService.insertLogs(dto.getId(), "A1", "B0", "프로젝트 생성", link);
    
    return dto.getId();
  }
  
  @Override
  public List<ProjectListResponse> selectProjectList(ProjectFilterRequest filter) {
    return projectMapper.selectProjectList(filter);
  }
  
  @Override
  public void updateProjectLock(String id) {
    projectMapper.updateProjectLock(id);
    String link = "/project/list";
    logsService.insertLogs(id, "A0", "B0", "프로젝트 잠금보관", link);
  }
  
  @Override
  public void updateUnProjectLock(String id) {
    projectMapper.updateUnProjectLock(id);
    String link = "/project/list";
    logsService.insertLogs(id, "A0", "B0", "프로젝트 잠금보관 해제", link);
  }
  
  @Override
  public void updateProjectStatus(String id) {
    projectMapper.updateProjectStatus(id);
    String link = "/project/list";
    logsService.insertLogs(id, "A3", "B0", "프로젝트 삭제", link);
  }
  
  @Override
  public ProjectInfoResponse findById(String id) {
    return projectMapper.findById(id);
  }
  
  @Override
  public ProjectInfoResponse updateProject(String id, ProjectUpdateRequest dto) {
    dto.setId(id);
    
    String link = "/project/" + dto.getId();
    logsService.insertLogs(id, "A2", "B0", "프로젝트 수정", link);
    
    projectMapper.updateProject(dto);
    return projectMapper.findById(id);
  }
  
}
