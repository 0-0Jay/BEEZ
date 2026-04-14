package com.beez.beez.task.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskTypeRequest {
  private String id;
  private String name;
  private String defaultStatus;
  private String description;

  private String copyFromId;
}
