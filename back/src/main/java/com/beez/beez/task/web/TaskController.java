package com.beez.beez.task.web;

import com.beez.beez.task.dto.TaskCategoryRequest;
import com.beez.beez.task.dto.TaskCategoryResponse;
import com.beez.beez.task.dto.TaskTypeRequest;
import com.beez.beez.task.dto.TaskTypeResponse;
import com.beez.beez.task.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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
}
