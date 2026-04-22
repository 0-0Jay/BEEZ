package com.beez.beez.task.dto;

import com.beez.beez.task.repository.TaskType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaskTypeResponse {
  private String id;
  private String name;
  private String defaultStatus;
  private String description;
  private Integer isUsed;
  
  public static TaskTypeResponse toDto(TaskType type) {
    return TaskTypeResponse.builder()
      .id(type.getId())
      .name(type.getName())
      .defaultStatus(type.getDefaultStatus())
      .description(type.getDescription())
      .build();
  }
  
}
