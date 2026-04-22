package com.beez.beez.project.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class ProjectMemberResponse {
  private String projectId;
  private List<MemberUserResponse> userList;
  private List<MemberGroupResponse> groupList;
  private List<MemberGroupMemberResponse> groupMemberList;
}
