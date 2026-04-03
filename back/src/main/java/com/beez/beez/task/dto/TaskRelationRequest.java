package com.beez.beez.task.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskRelationRequest {
  private String taskId;
  private String relatedTaskId;
  private String relationType;
  private String reverseType;
}
