package com.beez.beez.project.dto;

import lombok.Data;

import java.util.List;

@Data
public class ProjectMemberUpdateRequest {
  private String memberId;
  private List<String> roleIds;
}
