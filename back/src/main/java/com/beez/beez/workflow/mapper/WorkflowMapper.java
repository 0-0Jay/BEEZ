package com.beez.beez.workflow.mapper;

import com.beez.beez.workflow.dto.WorkflowResponse;
import com.beez.beez.workflow.dto.WorkflowSaveRequest;

import java.util.List;

public interface WorkflowMapper {

  // 업무흐름 기본설정 조회
  List<WorkflowResponse> findWorkflowMatrix(String roleId, String typeId, String conditionType);

  // 업무흐름 기본정보 등록
  void insertWorkflowConfig(WorkflowSaveRequest dto);

  // 업무흐름 삭제
  void deleteWorkflowConfig(WorkflowSaveRequest dto);
}
