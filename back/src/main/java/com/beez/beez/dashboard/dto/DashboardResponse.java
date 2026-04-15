package com.beez.beez.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardResponse {
  private String userId;
  private String projectId;
  private String pmpl;
  private LocalDate startDate;
  private LocalDate endDate;
  private Integer progress;
  private List<ProjectRecapResponse> projectList;
  private List<TaskRecapResponse> taskList;
  private List<MemoResponse> memoList;
  private List<ScheduleResponse> scheduleList;
  private List<MemberListResponse> memberList;
}
