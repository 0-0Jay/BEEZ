package com.beez.beez.dashboard.mapper;

import com.beez.beez.dashboard.dto.DashboardResponse;
import com.beez.beez.dashboard.dto.MemoRequest;
import org.apache.ibatis.annotations.Param;

public interface DashboardMapper {
  void findMainDashboard(DashboardResponse dashboardResponse);
  
  void findProjectDashboard(DashboardResponse dashboardResponse);
  
  void insertMemo(MemoRequest memoRequest);
  
  void updateMemo(MemoRequest memoRequest);
  
  void deleteMemo(String id);
}
