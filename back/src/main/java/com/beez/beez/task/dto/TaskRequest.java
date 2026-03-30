package com.beez.beez.task.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskRequest {
  private String isPublic;
  private String title;
  private String type;
  private String workflow;
  private String priority;
  private String description;
  private String parentId;
  private String category;
  private String userId;
  private String targetVersion;
  private String plannedStart;
  private String plannedEnd;
  private String actualStart;
  private String actualEnd;
  private Integer estimatedtime;
  private Integer progress;
  private boolean linkCopied;
  private boolean copySubTasks;
  private String fileId;
  private List<String> watcherIds;
}
