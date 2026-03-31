package com.beez.beez.task.dto;

import com.beez.beez.task.repository.Task;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskResponse {
  private String id;
  private String versionId;
  private String title;
  private String description;
  private String userId;
  private String type;
  private String category;
  private String workflow;
  private String priority;
  private String reject;
  private LocalDateTime plannedStart;
  private LocalDateTime plannedEnd;
  private LocalDateTime actualStart;
  private LocalDateTime actualEnd;
  private Integer estimatedTime;
  private Integer progress;
  private String parentId;
  private String isPublic;
  private String creator;
  private String fileId;
  private List<JournalResponse> journalList;
  private List<TaskTimeResponse> timeList;
  private List<TaskReplyResponse> replyList;
  private List<FileDetailResponse> fileList;
  private List<TaskListResponse> childTaskList;
  private List<TaskListResponse> linkedTaskList;
  
  public static TaskResponse toDto(Task task) {
    return TaskResponse.builder()
      .id(task.getId())
      .versionId(task.getVersionId())
      .title(task.getTitle())
      .description(task.getDescription())
      .userId(task.getUserId())
      .type(task.getType())
      .category(task.getCategory())
      .workflow(task.getWorkflow())
      .priority(task.getPriority())
      .reject(task.getReject())
      .plannedStart(task.getPlannedStart())
      .plannedEnd(task.getPlannedEnd())
      .actualStart(task.getActualStart())
      .actualEnd(task.getActualEnd())
      .estimatedTime(task.getEstimatedTime())
      .progress(task.getProgress())
      .parentId(task.getParentId())
      .isPublic(task.getIsPublic())
      .creator(task.getCreator())
      .fileId(task.getFileId())
      .build();
  }
}
