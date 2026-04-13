package com.beez.beez.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskRecapResponse {
  private String id;
  private String title;
  private String projectId;
  private String projectTitle;
  private int process;
  private String workflow;
  private LocalDate plannedEnd;
}
