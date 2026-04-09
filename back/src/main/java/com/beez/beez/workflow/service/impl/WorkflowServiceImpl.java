package com.beez.beez.workflow.service.impl;

import com.beez.beez.workflow.dto.WorkflowResponse;
import com.beez.beez.workflow.dto.WorkflowSaveRequest;
import com.beez.beez.workflow.mapper.WorkflowMapper;
import com.beez.beez.workflow.service.WorkflowService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class WorkflowServiceImpl implements WorkflowService {

  private final WorkflowMapper workflowMapper;

  // 업무흐름 조회
  @Override
  public List<WorkflowResponse> findWorkflowMatrix(String roleId, String typeId, String conditionType) {
    return workflowMapper.findWorkflowMatrix(roleId, typeId, conditionType);
  }

  // 업무흐름 등록
  @Override
  public void saveWorkflow(WorkflowSaveRequest dto) {
    try{
      workflowMapper.saveWorkflow(dto);
    } catch (Exception e) {
      log.error("업무흐름 등록 오류: roleId={}, typeId={}", dto.getRoleId(), dto.getTypeId(), e);
      throw new RuntimeException("업무흐름 등록 중 오류가 발생하였습니다.");
    }
  }

  // 일감 상태 공통 코드 조회
  @Override
  public List<Map<String, Object>> findTaskStatusCode(String groupValue) {
    return workflowMapper.findTaskStatusCode(groupValue);
  }
}
