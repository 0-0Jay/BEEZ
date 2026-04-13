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
public class ProjectRecapResponse {
  private String id;
  private String title;
  private LocalDate endDate;
  private int process;
}
