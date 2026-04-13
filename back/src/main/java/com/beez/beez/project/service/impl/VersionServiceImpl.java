package com.beez.beez.project.service.impl;

import com.beez.beez.aop.Loggable;
import com.beez.beez.logs.service.LogsService;
import com.beez.beez.project.dto.ProjectInfoResponse;
import com.beez.beez.project.dto.VersionCreateRequest;
import com.beez.beez.project.dto.VersionFilterRequest;
import com.beez.beez.project.dto.VersionListResponse;
import com.beez.beez.project.mapper.ProjectMapper;
import com.beez.beez.project.mapper.VersionMapper;
import com.beez.beez.project.service.VersionService;
import com.beez.beez.task.dto.CommonCodeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@RequiredArgsConstructor
@Service
public class VersionServiceImpl implements VersionService {
  
  private final ProjectMapper projectMapper;
  private final VersionMapper versionMapper;
  
  //버전 생성
  @Loggable(
    logType = "A1",
    logCategory = "B1",
    content = "버전 생성({id})",
    link = "/project/setting/{projectId}/versions",
    idField = "projectId"
  )
  @Override
  public void insertVersion(VersionCreateRequest dto) {
    ProjectInfoResponse project = projectMapper.findById(dto.getProjectId());
    
    if (project.getParentId() != null) {
      throw new IllegalArgumentException("하위 프로젝트에서는 버전을 생성할 수 없습니다.");
    }
    
    // 프론트 Boolean -> 코드값 변환
    dto.setIsShare(Boolean.TRUE.equals(dto.getIsShareYn()) ? "O1" : "O0");
    dto.setIsDefault(Boolean.TRUE.equals(dto.getIsDefaultYn()) ? "M1" : "M0");
    
    versionMapper.insertVersion(dto);
    
    if(dto.getIsDefault().equals("M1")) {
      projectMapper.updateDefaultVersion(dto.getProjectId(), dto.getId());
    }
  }
  
  //버전 수정
  @Loggable(
    logType = "A2",
    logCategory = "B1",
    content = "버전 수정({id})",
    link = "/project/setting/{projectId}/versions",
    idField = "projectId"
  )
  @Override
  public void updateVersion(VersionCreateRequest dto) {
    ProjectInfoResponse project = projectMapper.findById(dto.getProjectId());
    
    if (project.getParentId() != null) {
      throw new IllegalArgumentException("하위 프로젝트에서는 버전을 수정할 수 없습니다.");
    }
    
    // 프론트 Boolean -> 코드값 변환
    dto.setIsShare(Boolean.TRUE.equals(dto.getIsShareYn()) ? "O1" : "O0");
    dto.setIsDefault(Boolean.TRUE.equals(dto.getIsDefaultYn()) ? "M1" : "M0");
    
    versionMapper.updateVersion(dto);
    
    if(dto.getIsDefault().equals("M1")) {
      projectMapper.updateDefaultVersion(dto.getProjectId(), dto.getId());
    } else {
      // 기본 버전 체크 해제 시 → 현재 프로젝트 기본 버전이 이 버전일 때만 해제
      String currentDefaultVersionId = projectMapper.findDefaultVersionId(dto.getProjectId());
      
      if (dto.getId().equals(currentDefaultVersionId)) {
        projectMapper.updateDefaultVersion(dto.getProjectId(), null);
      }
    }

  }
  
  //버전 삭제
  @Loggable(
    logType = "A3",
    logCategory = "B1",
    content = "버전 삭제({id})",
    link = "/project/setting/{projectId}/versions",
    idField = "projectId"
  )
  @Override
  public void deleteVersion(String id, String projectId, String versionName) {
    ProjectInfoResponse projectInfo = projectMapper.findById(projectId);
    
    if (projectInfo.getParentId() != null) {
      throw new IllegalArgumentException("하위 프로젝트에서는 버전을 삭제할 수 없습니다.");
    }
    
    int result = versionMapper.deleteVersion(id);
    
    if (result == 0) {
      throw new IllegalArgumentException("일감에서 사용 중인 버전은 삭제할 수 없습니다.");
    }
    
    //기본버전이었으면 null로
    ProjectInfoResponse project = projectMapper.findById(projectId);
    if (id.equals(project.getDefaultVersionId())) {
      projectMapper.updateDefaultVersion(projectId, null);
    }

  }
  
  //버전 목록 조회(필터링)
  @Override
  public List<VersionListResponse> selectVersionList(VersionFilterRequest filter) {
    return versionMapper.selectVersionList(filter);
  }
  
  // 공통코드 목록
  @Override
  public List<CommonCodeResponse> findCommonCodeList() {
    return versionMapper.findCommonCodeList();
  }
  
  
}
