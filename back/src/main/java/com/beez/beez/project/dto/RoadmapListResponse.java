package com.beez.beez.project.dto;

import lombok.Data;

import java.util.Date;

@Data
public class RoadmapListResponse {
  // 버전 정보
  private String versionId;
  private String versionName;
  private String versionStatus;    // N0/N1 (프론트에서 변환)
  private String endDate;
  private String description;
  private String projectId;
  private String projectName;

  // 일감 정보 (일감 없는 버전은 null)
  private String taskId;
  private String taskTitle;
  private String taskProjectId;
  private String taskProjectName;
  private String taskUserId;
  private String taskUserName;
  private String taskType;
  private String taskTypeName;
  private String taskCategory;
  private String taskCategoryName;
  private String taskWorkflow;     // Q0~Q4 (프론트에서 변환)
  private String taskPriority;     // (프론트에서 변환)
  private String taskStatus;
  private Integer progress;
  private Integer subProgress;
  private String plannedStart;
  private String plannedEnd;
  private Integer estimatedTime;
  private String taskCreator;
  private Integer totalSpent;

}
