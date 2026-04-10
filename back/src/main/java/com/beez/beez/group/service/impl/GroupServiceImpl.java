package com.beez.beez.group.service.impl;

import com.beez.beez.group.dto.GroupInsertRequest;
import com.beez.beez.group.dto.GroupListResponse;
import com.beez.beez.group.dto.GroupUpdateRequest;
import com.beez.beez.group.mapper.GroupMapper;
import com.beez.beez.group.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class GroupServiceImpl implements GroupService {

  private final GroupMapper groupMapper;

  // 그룹 목록 조회
  @Override
  public List<GroupListResponse> findAllGroup(String name) {
    return groupMapper.findAllGroup(name);
  }

  // 그룹 등록
  @Override
  public void insertGroup(GroupInsertRequest dto) {
    try {
      groupMapper.insertGroup(dto);
    } catch (Exception e) {
      throw new RuntimeException(e.getMessage());
    }
  }

  // 그룹 수정
  @Override
  public void updateGroup(GroupUpdateRequest dto) {
    try {
      groupMapper.updateGroup(dto);
    } catch (Exception e) {
      throw new RuntimeException(e.getMessage());
    }
  }

  // 그룹 삭제
  @Override
  public void deleteGroup(String id) {
    try {
      groupMapper.deleteGroup(id);
    } catch (Exception e) {
      throw new RuntimeException(e.getMessage());
    }
  }
}
