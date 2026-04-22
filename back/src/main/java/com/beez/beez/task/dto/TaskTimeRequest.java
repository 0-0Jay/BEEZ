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
public class TaskTimeRequest {
  private String taskId;
  private LocalDateTime taskStart;
  private Integer spent;
  private String description;
  private Integer progress;
  private String activityType;
  private String userId;
}
