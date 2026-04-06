package com.beez.beez.project.dto;

import lombok.Data;

@Data
public class MemberGroupResponse {
  private String ProjectMemberId;
  private String groupId;
  private String groupName;
  private String roleId;
  private String roleName;
}
