package com.beez.beez.task.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaskReplyResponse {
  private String id;
  private String userId;
  private String name;
  private String content;
  private Timestamp createdOn;
  private Timestamp editedOn;
  private String parentId;
}
