package com.beez.beez.workflow.dto;

import lombok.Data;

import java.util.List;

@Data
public class WorkflowSaveRequest {

  private String roleId;
  private String typeId;
  private List<WorkflowItemRequest> details;
}
