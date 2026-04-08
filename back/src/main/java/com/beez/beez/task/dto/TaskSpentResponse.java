package com.beez.beez.task.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskSpentResponse {
  private String taskId;
  private String taskTitle;
  private String projectId;
  private String projectTitle;
  private String type;
  private String category;
  private String userId;
  private String name;
  private String description;
  private String activityType;
  private LocalDateTime taskStart;
  private int spent;
}
