package com.beez.beez.task.repository;

import com.beez.beez.task.dto.TaskTypeRequest;
import com.beez.beez.task.dto.TaskTypeResponse;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name="task_type")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskType {
  @Id
  private String id;
  private String name;
  private String defaultStatus;
  private String description;
  
  public static TaskType toEntity(TaskTypeRequest dto) {
    return TaskType.builder()
      .id(dto.getId())
      .name(dto.getName())
      .defaultStatus(dto.getDefaultStatus())
      .description(dto.getDescription())
      .build();
  }
}
