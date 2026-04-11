package com.beez.beez.task.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskWorkflowResponse {
  private String roleId;
  private String typeId;
  private String before;
  private String after;
  private String isAllow;
  private String conditionType;
}
