package com.beez.beez.project.dto;

import lombok.Data;

@Data
public class MemberUserResponse {
  private String projectMemberId;
  private String userId;
  private String userName;
  private String roleId;
  private String roleName;
  private String fixedReason;
}
