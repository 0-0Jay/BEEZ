package com.beez.beez.group.mapper;

import com.beez.beez.group.dto.*;

import java.util.List;
import java.util.Map;

public interface GroupMapper {

  // 그룹 목록 조회
  List<GroupListResponse> findAllGroup(String name);

  int countProjectByGroupId(String id);

  // 그룹 기본 정보 단건 조회
  Map<String, Object> findByIdGroups(String id);

  // 그룹 소속 사원 조회
  List<GroupMemberResponse> findGroupMembers(String groupId);

  // 참여 프로젝트 및 역할 조회
  List<GroupProjectResponse> findGroupProjects(String groupId);

  // 그룹 이름 중복 체크
  int checkGroupNameExists(String name, String id);

  // 그룹 등록
  void insertGroup(GroupInsertRequest dto);

  // 그룹 수정
  void updateGroup(GroupUpdateRequest dto);

  // 그룹 삭제
  void deleteGroup(String id);
}
