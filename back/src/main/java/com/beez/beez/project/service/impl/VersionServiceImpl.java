package com.beez.beez.project.service.impl;

import com.beez.beez.logs.service.LogsService;
import com.beez.beez.project.dto.ProjectInfoResponse;
import com.beez.beez.project.dto.VersionCreateRequest;
import com.beez.beez.project.mapper.ProjectMapper;
import com.beez.beez.project.mapper.VersionMapper;
import com.beez.beez.project.service.VersionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@RequiredArgsConstructor
@Service
public class VersionServiceImpl implements VersionService {
  
  private final ProjectMapper projectMapper;
  private final VersionMapper versionMapper;
  private final LogsService logsService;
  
  //  세션에서 아이디 받아오기
  private String getCurrentUserId() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    return auth.getName();
  }
  
  //버전 생성
  @Override
  public void insertVersion(VersionCreateRequest dto) {
    String userId = getCurrentUserId();
    
    versionMapper.insertVersion(dto);
    
    if(dto.getIsDefault().equals("M1")) {
      projectMapper.updateDefaultVersion(dto.getProjectId(), dto.getId());
    }
    
    String link = "/project/setting/" + dto.getProjectId() + "?tab=version";
    String content = String.format("버전 생성 (버전명 : %s)", dto.getName());
    logsService.insertLogs(dto.getId(), "A1", "B1", content, link, userId);
  }
  
  //버전 수정
  @Override
  public void updateVersion(VersionCreateRequest dto) {
    String userId = getCurrentUserId();
    
    versionMapper.updateVersion(dto);
    
    if(dto.getIsDefault().equals("M1")) {
      projectMapper.updateDefaultVersion(dto.getProjectId(), dto.getId());
    } else if (dto.getIsDefault().equals("M0")) {
      projectMapper.updateDefaultVersion(dto.getProjectId(), null);
    }
    
    String link = "/project/setting/" + dto.getProjectId() + "?tab=version";
    String content = String.format("버전 수정 (버전명 : %s)", dto.getName());
    logsService.insertLogs(dto.getId(), "A2", "B1", content, link, userId);
  }
  
  //버전 삭제
  @Override
  public void deleteVersion(String id, String projectId, String versionName) {
    String userId = getCurrentUserId();
    int result = versionMapper.deleteVersion(id);
    
    if (result == 0) {
      throw new IllegalArgumentException("일감에서 사용 중인 버전은 삭제할 수 없습니다.");
    }
    //기본버전이었으면 null로
    ProjectInfoResponse project = projectMapper.findById(projectId);
    if (id.equals(project.getDefaultVersionId())) {
      projectMapper.updateDefaultVersion(projectId, null);
    }
    
    String link = "/project/setting/" + projectId + "?tab=version";
    String content = String.format("버전 삭제 (버전명 : %s)", versionName);
    logsService.insertLogs(projectId, "A3", "B1", content, link, userId);
  }
  
  
}
