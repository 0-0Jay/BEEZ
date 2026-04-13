package com.beez.beez.dashboard.service;


import com.beez.beez.dashboard.dto.DashboardResponse;
import com.beez.beez.dashboard.dto.MemoRequest;

public interface DashboardService {
  DashboardResponse findMainDashboard(String id);
  
  DashboardResponse findProjectDashboard(String projectId, String id);
  
  void insertMemo(MemoRequest memoRequest);
  
  void updateMemo(MemoRequest memoRequest);
  
  void deleteMemo(String id);
}
