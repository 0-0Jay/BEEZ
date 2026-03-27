package com.beez.beez.project.mapper;

import com.beez.beez.project.dto.ProjectCreateRequest;
import com.beez.beez.project.dto.ProjectFilterRequest;
import com.beez.beez.project.dto.ProjectListResponse;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ProjectMapper {
  
  //프로젝트 등록
  void insertProject(ProjectCreateRequest dto);
  
  //프로젝트 목록 조회(필터링)
  List<ProjectListResponse> selectProjectList(ProjectFilterRequest filter);

  //프로젝트 잠금보관
  void updateProjectLock(String id);
  
  //프로젝트 잠금보관 해제
  void updateUnProjectLock(String id);
  
  //프로젝트 삭제
  void updateProjectStatus(String id);
  

}
