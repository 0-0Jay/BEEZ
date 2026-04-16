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
public class TaskWorkflowRequest {
  private List<String> roleId;
  private String typeId;
  private String conditionType;
  private List<TaskWorkflowResponse> workflows;
}
