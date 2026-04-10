package com.beez.beez.calendar.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CalendarResponse {
  private String id;
  private String userId;
  private String name;
  private String type;
  private String title;
  private String content;
  private String projectId;
  private LocalDate startDate;
  private LocalDate endDate;
}
