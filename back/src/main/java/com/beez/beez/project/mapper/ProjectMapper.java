package com.beez.beez.project.mapper;

import com.beez.beez.project.dto.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

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
  
  //프로젝트 단건 조회
  ProjectInfoResponse findById(String id);

  //프로젝트 수정
  void updateProject(ProjectUpdateRequest dto);
  
  //프로젝트 기본버전
  void updateDefaultVersion(@Param("projectId") String projectId, @Param("versionId") String versionId);
  
}
