package com.beez.beez.task.mapper;

import com.beez.beez.task.dto.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TaskMapper {
  List<TaskListResponse> findTaskList(@Param("projectId") String projectId, @Param("userId") String userId);
  
  List<MemberResponse> findMemberList(String projectId);
  
  List<VersionResponse> findVersionList(String projectId);
  
  String findLastFileId();
  
  void insertFileDetail();
  
  void insertTask(TaskRequest task);
  
  void findTaskDetail(TaskResponse task);
  
  void insertTaskReply(TaskReplyRequest taskReplyRequest);
  
  List<CommonCodeResponse> findCommonCodeList();
}
