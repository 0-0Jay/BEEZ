package com.beez.beez.task.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskRequest {
  private String id;
  private String projectId;
  private String version;
  private String title;
  private String description;
  private String userId;
  private String type;
  private String category;
  private String workflow;
  private String priority;
  private LocalDateTime plannedStart;
  private LocalDateTime plannedEnd;
  private LocalDateTime actualStart;
  private LocalDateTime actualEnd;
  private Integer estimatedTime;
  private Integer progress;
  private String parentId;
  private String isPublic;
  private String creator;
  private String fileId;
  private boolean linkCopied;
  private boolean copySubTasks;
  private List<String> watcherIds;
}
