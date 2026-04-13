package com.beez.beez.project.dto;

import lombok.Data;

@Data
public class RoadmapFilterRequest {
  private String projectId;
  private String versionStatus;  // ALL / N1 / N0
  private String versionId;
  private String taskType;
  private boolean includeSubProject;
}
