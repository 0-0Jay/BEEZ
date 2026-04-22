package com.beez.beez.task.dto;

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
public class TaskTimeResponse {
  private String id;
  private String userId;
  private String name;
  private Timestamp taskStart;
  private Integer spent;
  private String description;
  private Integer progress;
  private String activityType;
}
