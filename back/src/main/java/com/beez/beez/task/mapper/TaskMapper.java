package com.beez.beez.task.mapper;

import com.beez.beez.task.dto.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TaskMapper {
  List<TaskListResponse> findTaskList(@Param("projectId") String projectId, @Param("userId") String userId);
  
  List<MemberResponse> findMemberList(String projectId);
  
  List<VersionResponse> findVersionList(String projectId);
  
  void insertTask(TaskRequest task);
  
  void findTaskDetail(TaskResponse task);
  
  void insertTaskReply(TaskReplyRequest taskReplyRequest);
  
  List<CommonCodeResponse> findCommonCodeList();
  
  void updateTask(TaskRequest task);
  
  List<JournalDetailResponse> findJournalDetailList(String id);
  
  void insertTaskRelation(TaskRelationRequest taskRelationRequest);
  
  void deleteTask(String id);
  
  void copySubTasks(@Param("taskId") String taskId, @Param("copiedTaskId") String copiedTaskId);
  
  void insertTaskTime(TaskTimeRequest taskTimeRequest);
  
  void deleteTaskLink(String id);
  
  void calcSubProgress(@Param("taskId") String taskId,@Param("progress") int progress);
  
  List<TaskOverviewResponse> findTaskOverview(String id);
  
  List<TaskSpentResponse> findSpentOverview(String id);
  
  List<GanttDataResponse> findGanttData(String id);
}
