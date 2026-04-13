package com.beez.beez.project.dto;

import lombok.Data;

import java.util.Date;

@Data
public class RoadmapListResponse {
  // 버전 정보
  private String versionId;
  private String versionName;
  private String versionStatus;
  private Date endDate;
  private String description;
  private String projectId;

  // 일감 정보
  private String taskId;
  private String taskTitle;
  private String taskType;
  private String taskWorkflow;
  private String taskStatus;
  private Integer progress;
}
