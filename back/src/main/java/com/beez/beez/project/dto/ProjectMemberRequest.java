package com.beez.beez.project.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class ProjectMemberRequest {
  private String projectId;
  private List<String> userIds;
  private List<String> groupIds;
  private List<String> roleIds;
}
