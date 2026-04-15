package com.beez.beez.task.dto;

import com.beez.beez.file.dto.FileDetailResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskResponse {
  private String id;
  private String status;
  private String versionId;
  private String versionName;
  private String title;
  private String description;
  private String userId;
  private String name;
  private String type;
  private String category;
  private String workflow;
  private String priority;
  private String reject;
  private Timestamp plannedStart;
  private Timestamp plannedEnd;
  private Timestamp actualStart;
  private Timestamp actualEnd;
  private BigDecimal estimatedTime;
  private BigDecimal progress;
  private BigDecimal subProgress;
  private String parentId;
  private String parentName;
  private String isPublic;
  private String creator;
  private String fileId;
  private List<JournalResponse> journalList;
  private List<TaskTimeResponse> timeList;
  private List<TaskReplyResponse> replyList;
  private List<FileDetailResponse> fileList;
  private List<TaskListResponse> childTaskList;
  private List<TaskRelationResponse> linkedTaskList;
}
