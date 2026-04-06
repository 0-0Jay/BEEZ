package com.beez.beez.task.web;

import com.beez.beez.task.dto.TaskCategoryRequest;
import com.beez.beez.task.dto.TaskCategoryResponse;
import com.beez.beez.task.dto.TaskTypeRequest;
import com.beez.beez.task.dto.TaskTypeResponse;
import com.beez.beez.task.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class TaskOptionController {
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
}
