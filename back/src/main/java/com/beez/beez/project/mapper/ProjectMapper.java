package com.beez.beez.project.mapper;

import com.beez.beez.project.dto.ProjectCreateRequest;
import com.beez.beez.project.dto.ProjectListResponse;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ProjectMapper {
  
  //프로젝트 등록
  void insertProject(ProjectCreateRequest dto);
  
  //프로젝트 목록 조회
  public List<ProjectListResponse> findAllProjects();

}
