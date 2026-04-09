package com.beez.beez.task.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GanttDataResponse {
  private String taskId;
  private String taskTitle;
  private String projectId;
  private String projectTitle;
  private String versionId;
  private String userId;
  private String name;
  private Timestamp plannedStart;
  private Timestamp plannedEnd;
  private Timestamp actualStart;
  private Timestamp actualEnd;
  private String priority;
  private String workflow;
  private String type;
  private String cate;
  private int progress;
  private int subProgress;
  private String parentId;
  private List<GanttRelationResponse> relation;
}
