package com.beez.beez.group.service;

import com.beez.beez.group.dto.GroupInsertRequest;
import com.beez.beez.group.dto.GroupListResponse;
import com.beez.beez.group.dto.GroupUpdateRequest;

import java.util.List;

public interface GroupService {

  // 그룹 목록 조회
  List<GroupListResponse> findAllGroup(String name);

  // 그룹 등록
  void insertGroup(GroupInsertRequest dto);

  // 그룹 수정
  void updateGroup(GroupUpdateRequest dto);

  // 그룹 삭제
  void deleteGroup(String id);
}
