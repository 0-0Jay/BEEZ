package com.beez.beez.task.web;

import com.beez.beez.task.dto.*;
import com.beez.beez.task.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/task")
public class TaskController {
  private final TaskService taskService;
  
  // 일감 목록 조회
  @GetMapping("/{projectId}/{userId}")
  public ResponseEntity<List<TaskListResponse>> findTaskList(@PathVariable String projectId, @PathVariable String userId) {
    return ResponseEntity.ok(taskService.findTaskList(projectId, userId));
  }
  
  // 담당자 목록 조회
  @GetMapping("/member/{projectId}")
  public ResponseEntity<List<MemberResponse>> findMemberList(@PathVariable String projectId) {
    System.out.println(projectId + ", " + taskService.findMemberList(projectId).size());
    return ResponseEntity.ok(taskService.findMemberList(projectId));
  }
  
  // 버전 목록
  @GetMapping("/version/{projectId}")
  public ResponseEntity<List<VersionResponse>> findVersionList(@PathVariable String projectId) {
    return ResponseEntity.ok(taskService.findVersionList(projectId));
  }
  
  // 일감 추가
  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<String> insertTask(@ModelAttribute TaskRequest task, @RequestParam(value = "attachments", required = false) List<MultipartFile> files) {
    return ResponseEntity.ok(taskService.insertTask(task, files));
  }
  
  // 일감 수정
  @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<Void> updateTask(@ModelAttribute TaskRequest task, @RequestParam(value = "attachments", required = false) List<MultipartFile> files) {
    taskService.updateTask(task, files);
    return ResponseEntity.ok().build();
  }
  
  // 일감 상세
  @GetMapping("/{id}")
  public ResponseEntity<TaskResponse> findTaskDetail(@PathVariable String id) {
    return ResponseEntity.ok(taskService.findTaskDetail(id));
  }
  
  // 업무 흐름 가져오기
  @GetMapping("/workflow")
  public ResponseEntity<List<TaskWorkflowResponse>> findWorkflow(@RequestParam String roleId, @RequestParam String typeId, @RequestParam String conditionType ) {
    System.out.println(roleId + typeId + conditionType);
    return ResponseEntity.ok(taskService.findWorkflow(TaskWorkflowRequest.builder()
        .roleId(roleId)
        .typeId(typeId)
        .conditionType(conditionType)
      .build()));
  }
  
  // 댓글 삽입
  @PostMapping("/reply")
  public ResponseEntity<Void> insertTaskReply(@RequestBody TaskReplyRequest taskReplyRequest) {
    taskService.insertTaskReply(taskReplyRequest);
    return ResponseEntity.ok().build();
  }
  
  // 공통코드 조회
  @GetMapping("/common")
  public ResponseEntity<List<CommonCodeResponse>> findCommonCodeList() {
    return ResponseEntity.ok(taskService.findCommonCodeList());
  }
  
  // 변경사항 상세 조회
  @GetMapping("/{id}/journal")
  public ResponseEntity<List<JournalDetailResponse>> findJournalDetailList(@PathVariable String id) {
    return ResponseEntity.ok(taskService.findJournalDetailList(id));
  }
  
  // 일감 연결
  @PostMapping("/link")
  public ResponseEntity<Void> insertTaskRelation(@RequestBody TaskRelationRequest taskRelationRequest) {
    taskService.insertTaskRelation(taskRelationRequest);
    return ResponseEntity.ok().build();
  }
  
  // 일감 삭제
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteTask(@PathVariable String id) {
    taskService.deleteTask(id);
    return ResponseEntity.ok().build();
  }
  
  // 소요시간 입력
  @PostMapping("/time")
  public ResponseEntity<Void> insertTaskTime(@RequestBody TaskTimeRequest taskTimeRequest) {
    taskService.insertTaskTime(taskTimeRequest);
    return ResponseEntity.ok().build();
  }
  
  // 일감 연결 끊기
  @DeleteMapping("/link/{id}")
  public ResponseEntity<Void> deleteTaskLink(@PathVariable String id) {
    taskService.deleteTaskLink(id);
    return ResponseEntity.ok().build();
  }
  
  // 일감 보고서
  @GetMapping("/overview/{id}")
  public ResponseEntity<List<TaskOverviewResponse>> findTaskOverview(@PathVariable String id) {
    return ResponseEntity.ok(taskService.findTaskOverview(id));
  }
}
