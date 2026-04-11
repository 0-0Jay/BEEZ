package com.beez.beez.group.service;

import com.beez.beez.group.dto.*;

import java.util.List;
import java.util.Map;

public interface GroupService {

  // 그룹 목록 조회
  List<GroupListResponse> findAllGroup(String name);

  // 그룹 정보 상세 조회
  Map<String, Object> findGroupDetail(String id);

  // 그룹 등록
  void insertGroup(GroupInsertRequest dto);

  // 그룹 수정
  void updateGroup(GroupUpdateRequest dto);

  // 그룹 삭제
  void deleteGroup(String id);
}
