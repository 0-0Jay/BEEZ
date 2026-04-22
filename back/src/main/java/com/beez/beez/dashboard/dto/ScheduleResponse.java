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
public class ScheduleResponse {
  private String id;
  private String title;
  private String content;
  private String projectTitle;
  private LocalDate startDate;
  private LocalDate endDate;
}
