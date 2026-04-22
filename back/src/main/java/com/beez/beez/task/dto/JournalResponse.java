package com.beez.beez.task.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JournalResponse {
  private String id;
  private String userId;
  private String name;
  private Timestamp createdOn;
  private String fieldName;
  private String oldValue;
  private String newValue;
}
