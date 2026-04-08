package com.beez.beez.workflow.service;

import com.beez.beez.workflow.dto.WorkflowResponse;
import com.beez.beez.workflow.dto.WorkflowSaveRequest;

import java.util.List;
import java.util.Map;

public interface WorkflowService {

  // 업무흐름 조회
  List<WorkflowResponse> findWorkflowMatrix(String roleId, String typeId, String conditionType);

  // 업무흐름 등록
  void insertWorkflow(WorkflowSaveRequest dto);

  // 일감 상태 공통 코드 조회
  List<Map<String,Object>> findTaskStatusCode(String groupValue);
}
