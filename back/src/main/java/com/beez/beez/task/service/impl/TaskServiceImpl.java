package com.beez.beez.task.service.impl;

import com.beez.beez.file.dto.FileDetailRequest;
import com.beez.beez.file.service.FileService;
import com.beez.beez.task.dto.*;
import com.beez.beez.task.mapper.TaskMapper;
import com.beez.beez.task.repository.*;
import com.beez.beez.task.service.TaskService;
import com.beez.beez.websocket.dto.NotificationRequest;
import com.beez.beez.websocket.service.NotificationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {
  private final TaskTypeRepository taskTypeRepository;
  private final TaskCategoryRepository taskCategoryRepository;
  private final FileService fileService;
  private final TaskMapper taskMapper;
  private final NotificationService notificationService;
  
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
  
  // 버전 목록
  @Override
  public List<VersionResponse> findVersionList(String projectId) {
    return taskMapper.findVersionList(projectId);
  }
  
  // 일감 등록 / 복사
  @Override
  public String insertTask(TaskRequest task, List<MultipartFile> files) {
    List<FileDetailRequest> fileDetails = fileService.saveFile(files);
    task.setFileDetails(fileDetails);
    taskMapper.insertTask(task);
    
    // 복사 후처리
    if (Boolean.TRUE.equals(task.getLinkCopied())) {
      // taskMapper.insertTaskRelation(task) 호출 위치
    }
    if (Boolean.TRUE.equals(task.getCopySubTasks())) {
      // taskMapper.copySubTasks(task) 호출 위치
    }
    
    // 일감 생성자가 일감 담당자와 다를 경우 일감 담당자에게 일감 부여됨을 알림
    if (!task.getCreator().equals(task.getUserId())) {
    
    }
    
    return task.getId();
  }
  
  // 일감 수정
  @Override
  public void updateTask(TaskRequest task, List<MultipartFile> files) {
    List<FileDetailRequest> fileDetails = fileService.saveFile(files);
    task.setFileDetails(fileDetails);
    taskMapper.updateTask(task);
    // 반려 또는 완료일경우 담당자에게 알림
    if (task.getWorkflow().equals("Q3") || task.getWorkflow().equals("Q4")) {
      notificationService.sendNotification(NotificationRequest.builder()
          .userId(task.getUserId())
          .content("일감의 진행 상태가 " + ((task.getWorkflow().equals("Q3")) ? "승인" : "반려") + " 되었습니다.")
          .link("/task/" + task.getId())
          .build());
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
  
  // 일감 댓글 작성
  @Override
  public void insertTaskReply(TaskReplyRequest taskReplyRequest) {
    taskMapper.insertTaskReply(taskReplyRequest);
  }
  
  // 공통코드 목록
  @Override
  public List<CommonCodeResponse> findCommonCodeList() {
    return taskMapper.findCommonCodeList();
  }
  
  // 변경사항 상세 목록
  @Override
  public List<JournalDetailResponse> findJournalDetailList(String id) {
    return taskMapper.findJournalDetailList(id);
  }
  
  // 일감 연결
  @Override
  public void insertTaskRelation(TaskRelationRequest taskRelationRequest){
    Map<String, String> typeMap = Map.of(
      "W0", "W0",
      "W1", "W1",
      "W2", "W3",
      "W3", "W2",
      "W4", "W5",
      "W5", "W4",
      "W6", "W7"
    );
    taskRelationRequest.setReverseType(typeMap.get(taskRelationRequest.getRelationType()));
    taskMapper.insertTaskRelation(taskRelationRequest);
  }
}
