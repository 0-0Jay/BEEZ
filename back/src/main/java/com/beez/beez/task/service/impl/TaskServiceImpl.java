package com.beez.beez.task.service.impl;

import com.beez.beez.task.dto.*;
import com.beez.beez.task.mapper.TaskMapper;
import com.beez.beez.task.repository.*;
import com.beez.beez.task.service.TaskService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {
  private final TaskTypeRepository taskTypeRepository;
  private final TaskCategoryRepository taskCategoryRepository;
  private final TaskRepository taskRepository;
  private final TaskMapper taskMapper;
  
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
  
  // 일감 목록 조회
  @Override
  public List<TaskListResponse> findTaskList(String projectId, String userId) {
    return taskMapper.findTaskList(projectId, userId);
  }
  
  // 담당자 목록 조회
  @Override
  public List<MemberResponse> findMemberList(String projectId) {
    return taskMapper.findMemberList(projectId);
  }
  
  // 우선순위 목록
  @Override
  public List<PriorityResponse> findPriorityList() {
    List<PriorityResponse> list = taskMapper.findPriorityList();
    return taskMapper.findPriorityList();
  }
  
  // 진행상태 목록
  @Override
  public List<WorkflowResponse> findWorkflowList() {
    return taskMapper.findWorkflowList();
  }
  
  // 버전 목록
  @Override
  public List<VersionResponse> findVersionList(String projectId) {
    return taskMapper.findVersionList(projectId);
  }
  
  // 일감 등록
  @Override
  @Transactional
  public void insertTask(TaskRequest task, List<MultipartFile> files) {
//    if (files != null && !files.isEmpty()) {
//      taskRepository.insertFiles(task.getUserId());
//      String fileId = taskMapper.findLastFileId();
//
//      for (MultipartFile file : files) {
//        String originalName = file.getOriginalFilename();
//        String extension = originalName.substring(originalName.lastIndexOf(".") + 1);
//        String storedName = UUID.randomUUID().toString() + "." + extension;
//        long fileSize = file.getSize();
//
//        FileDetailRequest fileDetail = FileDetailRequest.builder()
//          .fileId(fileId)
//          .originalName(originalName)
//          .storedName(storedName)
//          .extension(extension)
//          .fileSize(fileSize)
//          .build();
//
//        taskMapper.insertFileDetail(fileDetail);
//      }
//    }
    taskMapper.insertTask(task);
    if (task.getWatcherIds() != null && !task.getWatcherIds().isEmpty()) {
      taskMapper.insertTaskWatcher(task.getId(), task.getWatcherIds());
    }
  }
  
  // 일감 상세
  @Override
  public TaskResponse findTaskDetail(String id) {
    TaskResponse task = new TaskResponse();
    task.setId(id);
    taskMapper.findTaskDetail(task);
    return task;
  }
  
  // 관계 목록
  @Override
  public List<RelationResponse> findRelationList() {
    return taskMapper.findRelationList();
  }
  
  // 작업분류 목록
  @Override
  public List<ActivityResponse> findActivityList() {
    return taskMapper.findActivityList();
  }
  
  // 일감 댓글 작성
  @Override
  public void insertTaskReply(TaskReplyRequest taskReplyRequest) {
    taskMapper.insertTaskReply(taskReplyRequest);
  }
}
