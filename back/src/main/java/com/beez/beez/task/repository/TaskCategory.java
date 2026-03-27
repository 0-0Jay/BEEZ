package com.beez.beez.task.repository;

import com.beez.beez.task.dto.TaskCategoryRequest;
import com.beez.beez.task.dto.TaskTypeRequest;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name="task_category")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskCategory {
  @Id
  private String id;
  private String name;
  private String description;
  
  public static TaskCategory toEntity(TaskCategoryRequest dto) {
    return TaskCategory.builder()
      .id(dto.getId())
      .name(dto.getName())
      .build();
  }
}
