package com.beez.beez.task.mapper;

import com.beez.beez.task.dto.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TaskMapper {
  List<TaskListResponse> findTaskList(@Param("projectId") String projectId, @Param("userId") String userId);
  
  List<MemberResponse> findMemberList(String projectId);
  
  List<PriorityResponse> findPriorityList();
  
  List<WorkflowResponse> findWorkflowList();
  
  List<VersionResponse> findVersionList(String projectId);
  
  String findLastFileId();
  
  void insertFileDetail();
  
  void insertTask(TaskRequest task);
  
  void insertTaskWatcher(@Param("taskId") String taskId, @Param("watcherList") List<String> watcherList);
}
