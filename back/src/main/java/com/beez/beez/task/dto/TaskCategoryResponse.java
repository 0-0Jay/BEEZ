package com.beez.beez.task.dto;

import com.beez.beez.task.repository.TaskCategory;
import com.beez.beez.task.repository.TaskType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaskCategoryResponse {
  private String id;
  private String name;
  private String description;
  
  public static TaskCategoryResponse toDto(TaskCategory category) {
    return TaskCategoryResponse.builder()
      .id(category.getId())
      .name(category.getName())
      .build();
  }
  
}
