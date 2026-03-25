package com.beez.beez.project.service.impl;

import com.beez.beez.project.dto.ProjectCreateRequest;
import com.beez.beez.project.mapper.ProjectMapper;
import com.beez.beez.project.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@RequiredArgsConstructor
@Service
public class ProjectServiceImpl implements ProjectService {
  
  private final ProjectMapper projectMapper;
  
  @Override
  public String insertProject(ProjectCreateRequest dto) {
    dto.setUserId("20210001");
    projectMapper.insertProject(dto);
    return dto.getId();
  }
}
