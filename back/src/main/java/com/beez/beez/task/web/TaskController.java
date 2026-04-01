package com.beez.beez.task.web;

import com.beez.beez.task.dto.*;
import com.beez.beez.task.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
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
  public List<TaskTypeResponse> findTaskType() {
    return taskService.findTaskType();
  }
  
  // 일감 유형 생성
  @PostMapping("/type")
  public void insertTaskType(@RequestBody TaskTypeRequest request) {
    taskService.insertTaskType(request);
  }
  
  // 일감 유형 수정
  @PutMapping("/type")
  public void updateTaskType(@RequestBody TaskTypeRequest request) {
    taskService.updateTaskType(request);
  }
  
  // 일감 유형 삭제
  @DeleteMapping("/type/{id}")
  public void deleteTaskType(@PathVariable String id) {
    taskService.deleteTaskType(id);
  }
  
  // 일감 범주 목록
  @GetMapping("/category")
  public List<TaskCategoryResponse> findTaskCategory() {
    return taskService.findTaskCategory();
  }
  
  // 일감 범주 생성
  @PostMapping("/category")
  public void insertTaskCategory(@RequestBody TaskCategoryRequest request) {
    taskService.insertTaskCategory(request);
  }
  
  // 일감 범주 수정
  @PutMapping("/category")
  public void updateTaskCategory(@RequestBody TaskCategoryRequest request) {
    taskService.updateTaskCategory(request);
  }
  
  // 일감 범주 삭제
  @DeleteMapping("/category/{id}")
  public void deleteTaskCategory(@PathVariable String id) {
    taskService.deleteTaskCategory(id);
  }
  
  // 일감 목록 조회
  @GetMapping("/{projectId}/{userId}")
  public List<TaskListResponse> findTaskList(@PathVariable String projectId, @PathVariable String userId) {
    return taskService.findTaskList(projectId, userId);
  }
  
  // 담당자 목록 조회
  @GetMapping("/member/{projectId}")
  public List<MemberResponse> findMemberList(@PathVariable String projectId) {
    return taskService.findMemberList(projectId);
  }
  
  // 우선순위 목록
  @GetMapping("/priority")
  public List<PriorityResponse> findPriorityList() {
    return taskService.findPriorityList();
  }
  
  // 진행상태 목록
  @GetMapping("/workflow")
  public List<WorkflowResponse> findWorkflowList() {
    return taskService.findWorkflowList();
  }
  
  // 버전 목록
  @GetMapping("/version/{projectId}")
  public List<VersionResponse> findVersionList(@PathVariable String projectId) {
    return taskService.findVersionList(projectId);
  }
  
  // 일감 추가
  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public void insertTask(@ModelAttribute TaskRequest task, @RequestPart(value = "attachments", required = false) List<MultipartFile> files) {
    taskService.insertTask(task, files);
  }
  
  // 일감 상세
  @GetMapping("/{id}")
  public TaskResponse findTaskDetail(@PathVariable String id) {
    return taskService.findTaskDetail(id);
  }
  
  @GetMapping("/relation")
  public List<RelationResponse> findRelationList() {
    return taskService.findRelationList();
  }
  
  @GetMapping("/activity")
  public List<ActivityResponse> findActivityList() {
    return taskService.findActivityList();
  }
  
  @PostMapping("/reply")
  public void insertTaskReply(@RequestBody TaskReplyRequest taskReplyRequest) {
    taskService.insertTaskReply(taskReplyRequest);
  }
}
