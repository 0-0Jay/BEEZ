package com.beez.beez.group.service.impl;

import com.beez.beez.group.dto.*;
import com.beez.beez.group.mapper.GroupMapper;
import com.beez.beez.group.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

  // 그룹 정보 상세 조회
  @Override
  public Map<String, Object> findGroupDetail(String id) {
    Map<String, Object> info = groupMapper.findByIdGroups(id);
    List<GroupMemberResponse> members = groupMapper.findGroupMembers(id);
    List<GroupProjectResponse> projects = groupMapper.findGroupProjects(id);

//    System.out.println("info -> "+ info);

    Map<String, Object> result = new HashMap<>();
    result.put("groupInfo", info);
    result.put("members", members);
    result.put("projects", projects);

    return result;
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
    int projectCount = groupMapper.countProjectByGroupId(id);

    if (projectCount > 0) {
      throw new RuntimeException("USED_IN_PROJECT");
    }

    try {
      groupMapper.deleteGroup(id);
    } catch (Exception e) {
      throw new RuntimeException("삭제 중 서버 오류가 발생했습니다.");
    }
  }
}
