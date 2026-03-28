package com.beez.beez.task.service.impl;

import com.beez.beez.task.dto.TaskCategoryRequest;
import com.beez.beez.task.dto.TaskCategoryResponse;
import com.beez.beez.task.dto.TaskTypeRequest;
import com.beez.beez.task.dto.TaskTypeResponse;
import com.beez.beez.task.repository.*;
import com.beez.beez.task.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {
  private final TaskTypeRepository taskTypeRepository;
  private final TaskCategoryRepository taskCategoryRepository;
  private final TaskRepository taskRepository;
  
  // 일감 유형 목록
  public List<TaskTypeResponse> findTaskType() {
    return taskTypeRepository.findAll().stream().map(TaskTypeResponse::toDto).toList();
  }
  
  // 일감 유형 추가
  public void insertTaskType(TaskTypeRequest dto) {
    taskTypeRepository.insertTaskType(dto.getName(), dto.getDefaultStatus(), dto.getDescription());
  }
  
  // 일감 유형 수정
  public void updateTaskType(TaskTypeRequest dto) {
    taskTypeRepository.save(TaskType.toEntity(dto));
  }
  
  // 일감 유형 삭제
  public void deleteTaskType(String id) {
    taskTypeRepository.deleteById(id);
  }
  
  // 일감 범주 목록
  @Override
  public List<TaskCategoryResponse> findTaskCategory() {
    return taskCategoryRepository.findAll().stream().map(TaskCategoryResponse::toDto).toList();
  }
  
  // 일감 범주 추가
  @Override
  public void insertTaskCategory(TaskCategoryRequest dto) {
    taskCategoryRepository.insertTaskCategory(dto.getName(), dto.getDescription());
  }
  
  // 일감 범주 수정
  @Override
  public void updateTaskCategory(TaskCategoryRequest dto) {
    System.out.println(dto.getDescription());
    taskCategoryRepository.save(TaskCategory.toEntity(dto));
  }
  
  // 일감 범주 삭제
  @Override
  public void deleteTaskCategory(String id) {
    taskCategoryRepository.deleteById(id);
  }
  
}
