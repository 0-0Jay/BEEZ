package com.beez.beez.project.mapper;

import com.beez.beez.project.dto.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProjectMapper {
  
  //프로젝트 등록
  void insertProject(ProjectCreateRequest dto);
  
  //프로젝트 식별자 중복체크
  int countByIdentifier(String identifier);
  
  //프로젝트 타이틀 중복체크
  int countByTitle(String title);
  
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
  
  //프로젝트 수정 시 식별자 중복체크
  int countByIdentifierExclude(@Param("identifier") String identifier, @Param("projectId") String projectId);
  
  //프로젝트 수정 시 타이틀 중복 체크
  int countByTitleExclude(@Param("title") String title, @Param("projectId") String projectId);
  
  //프로젝트 기본버전 조회
  String findDefaultVersionId(String projectId);
  
  //프로젝트 기본버전 수정
  void updateDefaultVersion(@Param("projectId") String projectId, @Param("versionId") String versionId);
  
  //프로젝트 구성원 조회
  void findProjectMember(ProjectMemberResponse projectMember);
  
  //프로젝트 구성원 삭제
  void deleteProjectMember(String projectMemberId);
  
  //프로젝트 구성원 수정
  void updateProjectMember(ProjectMemberUpdateRequest dto);
  
  //역할 조회
  List<RolesResponse> findRoles();
  
  //사용자 조회
  List<UserResponse> findUsers(@Param("projectId") String projectId, @Param("keyword") String keyword);
  
  //그룹 조회
  List<GroupResponse> findGroups(@Param("projectId") String projectId, @Param("keyword") String keyword);
  
  //프로젝트 구성원 추가
  void insertProjectMember(ProjectMemberRequest dto);
  
}
