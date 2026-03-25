package com.beez.beez.project.service;

import com.beez.beez.project.dto.ProjectCreateRequest;

public interface ProjectService {
  
  // 프로젝트 등록
  String insertProject(ProjectCreateRequest dto);
  
}
