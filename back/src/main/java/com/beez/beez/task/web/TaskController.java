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
  
  // 일감 유형 목록
  @GetMapping("/type")
  public ResponseEntity<List<TaskTypeResponse>> findTaskType() {
    return ResponseEntity.ok(taskService.findTaskType());
  }
  
  // 일감 유형 생성
  @PostMapping("/type")
  public ResponseEntity<Void> insertTaskType(@RequestBody TaskTypeRequest request) {
    taskService.insertTaskType(request);
    return ResponseEntity.ok().build();
  }
  
  // 일감 유형 수정
  @PutMapping("/type")
  public ResponseEntity<Void> updateTaskType(@RequestBody TaskTypeRequest request) {
    taskService.updateTaskType(request);
    return ResponseEntity.ok().build();
  }
  
  // 일감 유형 삭제
  @DeleteMapping("/type/{id}")
  public ResponseEntity<Void> deleteTaskType(@PathVariable String id) {
    taskService.deleteTaskType(id);
    return ResponseEntity.ok().build();
  }
  
  // 일감 범주 목록
  @GetMapping("/category")
  public ResponseEntity<List<TaskCategoryResponse>> findTaskCategory() {
    return ResponseEntity.ok(taskService.findTaskCategory());
  }
  
  // 일감 범주 생성
  @PostMapping("/category")
  public ResponseEntity<Void> insertTaskCategory(@RequestBody TaskCategoryRequest request) {
    taskService.insertTaskCategory(request);
    return ResponseEntity.ok().build();
  }
  
  // 일감 범주 수정
  @PutMapping("/category")
  public ResponseEntity<Void> updateTaskCategory(@RequestBody TaskCategoryRequest request) {
    taskService.updateTaskCategory(request);
    return ResponseEntity.ok().build();
  }
  
  // 일감 범주 삭제
  @DeleteMapping("/category/{id}")
  public ResponseEntity<Void> deleteTaskCategory(@PathVariable String id) {
    taskService.deleteTaskCategory(id);
    return ResponseEntity.ok().build();
  }
  
  // 일감 목록 조회
  @GetMapping("/{projectId}/{userId}")
  public ResponseEntity<List<TaskListResponse>> findTaskList(@PathVariable String projectId, @PathVariable String userId) {
    return ResponseEntity.ok(taskService.findTaskList(projectId, userId));
  }
  
  // 담당자 목록 조회
  @GetMapping("/member/{projectId}")
  public ResponseEntity<List<MemberResponse>> findMemberList(@PathVariable String projectId) {
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
}
