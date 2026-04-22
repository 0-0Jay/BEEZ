package com.beez.beez.task.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskReplyRequest {
  String id;
  String taskId;
  String userId;
  String content;
  String createdOn;
  String editedOn;
  String parentId;
  String taskUser;
  String projectId;
}
