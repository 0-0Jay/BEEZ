package com.beez.beez.workflow.dto;

import lombok.Data;

import java.util.List;

@Data
public class WorkflowCopyRequest {

  // 원본
  private String sourceRoleId;
  private String sourceTypeId;

  // 대상
  private List<String> targetRoleIds;
  private List<String> targetTypeIds;
}
