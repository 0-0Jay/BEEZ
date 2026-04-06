package com.beez.beez.project.dto;

import lombok.Data;

@Data
public class MemberGroupMemberResponse {
  private String projectMemberId;
  private String userId;
  private String groupId;
  private String userName;
  private String roleId;
  private String roleName;
  private String isInherited;
}
