package com.beez.beez.task.dto;

import com.beez.beez.file.dto.FileDetailRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskRequest {
  private String id;
  private String projectId;
  private String versionId;
  private String title;
  private String description;
  private String userId;
  private String type;
  private String category;
  private String workflow;
  private String priority;
  @DateTimeFormat(pattern = "yyyy-MM-dd")
  private LocalDate plannedStart;
  @DateTimeFormat(pattern = "yyyy-MM-dd")
  private LocalDate plannedEnd;
  @DateTimeFormat(pattern = "yyyy-MM-dd")
  private LocalDate actualStart;
  @DateTimeFormat(pattern = "yyyy-MM-dd")
  private LocalDate actualEnd;
  private Integer estimatedTime;
  private Integer progress;
  private String parentId;
  private String isPublic;
  private String creator;
  private String fileId;
  private List<FileDetailRequest> fileDetails;
  private String editor;
  private List<JournalRequest> journals;
  private List<String> deletedFileIds;
  private Boolean linkCopied;
  private Boolean copySubTasks;
  private String originTask;
}
