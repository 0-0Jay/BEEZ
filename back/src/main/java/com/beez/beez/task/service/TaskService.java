package com.beez.beez.task.service;

import com.beez.beez.task.dto.*;
import org.springframework.web.multipart.MultipartFile;

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
  
  // 일감 목록
  List<TaskListResponse> findTaskList(String projectId, String userId);
  
  // 담당자 목록
  List<MemberResponse> findMemberList(String projectId);
  
  // 버전 목록
  List<VersionResponse> findVersionList(String projectId);
  
  // 일감 추가
  void insertTask(TaskRequest task, List<MultipartFile> files);
  
  void updateTask(TaskRequest task, List<MultipartFile> files);
  
  // 일감 상세
  TaskResponse findTaskDetail(String id);
  
  // 일감 댓글 작성
  void insertTaskReply(TaskReplyRequest taskReplyRequest);
  
  // 공통코드 목록
  List<CommonCodeResponse> findCommonCodeList();
}
