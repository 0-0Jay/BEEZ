package com.beez.beez.workflow.web;

import com.beez.beez.workflow.dto.WorkflowResponse;
import com.beez.beez.workflow.dto.WorkflowSaveRequest;
import com.beez.beez.workflow.service.WorkflowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/workflow")
public class WorkflowController {

  private final WorkflowService workflowService;

  // 업무흐름 조회
  @GetMapping("/list")
  public ResponseEntity<List<WorkflowResponse>> findWorkflowMatrix(@RequestParam(name = "roleId") String roleId,
                                              @RequestParam(name = "typeId") String typeId,
                                              @RequestParam(name = "conditionType") String conditionType){
    List<WorkflowResponse> list = workflowService.findWorkflowMatrix(roleId, typeId, conditionType);
    return ResponseEntity.ok(list);
  }

  // 업무흐름 저장
  @PostMapping("/create")
  public ResponseEntity<String> insertWorkflow(@RequestBody WorkflowSaveRequest dto){
    workflowService.insertWorkflow(dto);
    return ResponseEntity.ok("성공적으로 저장되었습니다.");
  }

  // 일감 상태 공통 코드 조회
  @GetMapping("/taskStatus/{groupValue}")
  public ResponseEntity<List<Map<String, Object>>> findTaskStatusCode(@PathVariable String groupValue){
    List<Map<String, Object>> list = workflowService.findTaskStatusCode(groupValue);
    return ResponseEntity.ok(list);
  }

}
