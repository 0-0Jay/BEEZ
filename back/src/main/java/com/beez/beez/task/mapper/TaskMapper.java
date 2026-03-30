package com.beez.beez.task.mapper;

import com.beez.beez.task.dto.*;

import java.util.List;

public interface TaskMapper {
  List<TaskListResponse> findTaskList(String projectId, String userId);
  
  List<MemberResponse> findMemberList(String projectId);
  
  List<PriorityResponse> findPriorityList();
  
  List<WorkflowResponse> findWorkflowList();
  
  List<VersionResponse> findVersionList(String projectId);
  
  String findLastFileId();
  
  void insertFileDetail();
  
  void insertTask(TaskRequest task);
}
