package com.beez.beez.task.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaskRelationResponse {
  private String id;
  private String title;
  private String workflow;
  private Timestamp plannedEnd;
  private String progress;
  private String relationType;
}
