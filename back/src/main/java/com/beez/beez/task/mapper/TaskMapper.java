package com.beez.beez.task.mapper;

import com.beez.beez.task.dto.TaskListResponse;

import java.util.List;

public interface TaskMapper {
  List<TaskListResponse> findTaskList(String projectId);
}
