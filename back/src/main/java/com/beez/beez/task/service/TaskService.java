package com.beez.beez.task.service;

import com.beez.beez.task.dto.TaskCategoryRequest;
import com.beez.beez.task.dto.TaskCategoryResponse;
import com.beez.beez.task.dto.TaskTypeRequest;
import com.beez.beez.task.dto.TaskTypeResponse;

import java.util.List;

public interface TaskService {
  // 일감 유형 목록
  List<TaskTypeResponse> findTaskType();
  
  // 일감 유형 생성
  void insertTaskType(TaskTypeRequest request);
  
  // 일감 유형 수정
  void updateTaskType(TaskTypeRequest request);
  
  // 일감 유형 삭제
  void deleteTaskType(String id);
  
  // 일감 범주 목록
  List<TaskCategoryResponse> findTaskCategory();
  
  // 일감 범주 생성
  void insertTaskCategory(TaskCategoryRequest request);
  
  // 일감 범주 수정
  void updateTaskCategory(TaskCategoryRequest request);
  
  // 일감 범주 삭제
  void deleteTaskCategory(String id);
  
}
