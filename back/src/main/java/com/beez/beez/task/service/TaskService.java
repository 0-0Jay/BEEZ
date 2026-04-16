package com.beez.beez.task.service;

import com.beez.beez.task.dto.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface TaskService {
  // 일감 유형 목록
  List<TaskTypeResponse> findTaskType();
  
  // 일감 유형 삭제
  void deleteTaskType(String id);

  // 일감 유형 생성 및 수정(+ 업무 흐름 복사)
  void saveTaskType(TaskTypeRequest dto);
  
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
  String insertTask(TaskRequest task, List<MultipartFile> files);
  
  void updateTask(TaskRequest task, List<MultipartFile> files);
  
  // 일감 상세
  TaskResponse findTaskDetail(String id);
  
  // 일감 댓글 작성
  void insertTaskReply(TaskReplyRequest taskReplyRequest);
  
  // 공통코드 목록
  List<CommonCodeResponse> findCommonCodeList();
  
  // 변경사항 상세 목록
  List<JournalDetailResponse> findJournalDetailList(String id);
  
  // 일감 연결
  void insertTaskRelation(TaskRelationRequest taskRelationRequest);
  
  // 일감 삭제
  void deleteTask(String id);
  
  // 소요시간 기록
  void insertTaskTime(TaskTimeRequest taskTimeRequest);
  
  // 일감 연결 끊기
  void deleteTaskLink(String id);
  
  // 일감 보고서
  List<TaskOverviewResponse> findTaskOverview(String id);
  
  // 소요시간
  List<TaskSpentResponse> findSpentOverview(String id);
  
  // 간트차트
  List<GanttDataResponse> findGanttData(String id);
  
  // 업무흐름
  List<TaskWorkflowResponse> findWorkflow(TaskWorkflowRequest taskWorkflowRequest);
  
  void deleteTaskReply(String id);
  
  void updateTaskReply(TaskReplyRequest taskReplyRequest);
}
