package com.beez.beez.workflow.dto;

import lombok.Data;

@Data
public class WorkflowResponse {

  private String beforeCode;
  private String beforeName;
  private String afterCode;
  private String afterName;
  private String isAllow;
  private String roleId;
  private String typeId;
  private String conditionType;
}
