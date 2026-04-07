package com.beez.beez.workflow.service;

import com.beez.beez.workflow.dto.WorkflowResponse;
import com.beez.beez.workflow.dto.WorkflowSaveRequest;

import java.util.List;

public interface WorkflowService {

  // 업무흐름 기본설정 조회
  List<WorkflowResponse> findWorkflowMatrix(String roleId, String typeId, String conditionType);

  // 업무흐름 등록
  void insertWorkflow(WorkflowSaveRequest dto);
}
