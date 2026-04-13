package com.beez.beez.dashboard.service.impl;

import com.beez.beez.dashboard.dto.DashboardResponse;
import com.beez.beez.dashboard.dto.MemoRequest;
import com.beez.beez.dashboard.mapper.DashboardMapper;
import com.beez.beez.dashboard.service.DashboardService;
import com.beez.beez.task.mapper.TaskMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {
  private final DashboardMapper dashboardMapper;
  
  @Override
  public DashboardResponse findMainDashboard(String id) {
    DashboardResponse dashboardResponse = new DashboardResponse();
    dashboardResponse.setUserId(id);
    dashboardMapper.findMainDashboard(dashboardResponse);
    return dashboardResponse;
  }
  
  @Override
  public DashboardResponse findProjectDashboard(String projectId, String id) {
    DashboardResponse dashboardResponse = new DashboardResponse();
    dashboardResponse.setUserId(id);
    dashboardResponse.setProjectId(projectId);
    dashboardMapper.findProjectDashboard(dashboardResponse);
    return dashboardResponse;
  }
  
  @Override
  public void insertMemo(MemoRequest memoRequest) {
    dashboardMapper.insertMemo(memoRequest);
  }
  
  @Override
  public void updateMemo(MemoRequest memoRequest) {
    dashboardMapper.updateMemo(memoRequest);
  }
  
  @Override
  public void deleteMemo(String id) {
    dashboardMapper.deleteMemo(id);
  }
}
