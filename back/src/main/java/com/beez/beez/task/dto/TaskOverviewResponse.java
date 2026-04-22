package com.beez.beez.task.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskOverviewResponse {
  private String type;
  private String category;
  private String priority;
  private String workflow;
  private String userId;
  private String creator;
  private String versionId;
}
