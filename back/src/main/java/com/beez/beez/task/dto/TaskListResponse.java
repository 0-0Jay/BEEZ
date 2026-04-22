package com.beez.beez.task.dto;

import com.beez.beez.task.repository.Task;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskListResponse {
  private String id;
  private String title;
  private String userId;
  private String userName;
  private String type;
  private String category;
  private String workflow;
  private String priority;
  private String plannedStart;
  private Timestamp plannedEnd;
  private int progress;
  private String parentId;
  private String versionId;
  private String isPublic;
  private String creator;
}
