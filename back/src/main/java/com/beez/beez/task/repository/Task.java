package com.beez.beez.task.repository;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
public class Task {
  @Id
  private String id;
  private String projectId;
  private String versionId;
  private String title;
  private String description;
  private String userId;
  private String type;
  private String category;
  private String workflow;
  private String reject;
  private String priority;
  private LocalDateTime plannedStart;
  private LocalDateTime plannedEnd;
  private LocalDateTime actualStart;
  private LocalDateTime actualEnd;
  private int estimatedTime;
  private int progress;
  private String status;
  private String parentId;
  private String isPublic;
  private String creator;
  private String fileId;
}
