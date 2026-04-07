package com.beez.beez.workflow.dto;

import lombok.Data;

@Data
public class WorkflowItemRequest {

  private String beforeCode;
  private String afterCode;
  private String isAllow;
}
