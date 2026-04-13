package com.beez.beez.dashboard.web;

import com.beez.beez.dashboard.dto.DashboardResponse;
import com.beez.beez.dashboard.dto.MemoRequest;
import com.beez.beez.dashboard.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {
  private final DashboardService dashboardService;
  
  @GetMapping("/{id}")
  public ResponseEntity<DashboardResponse> findMainDashboard(@PathVariable String id){
    return ResponseEntity.ok(dashboardService.findMainDashboard(id));
  }
  
  @GetMapping("/{projectId}/{id}")
  public ResponseEntity<DashboardResponse> findMainDashboard(@PathVariable String projectId, @PathVariable String id){
    return ResponseEntity.ok(dashboardService.findProjectDashboard(projectId, id));
  }
  
  @PostMapping("/memo")
  public ResponseEntity<Void> insertMemo(@RequestBody MemoRequest memoRequest) {
    dashboardService.insertMemo(memoRequest);
    return ResponseEntity.ok().build();
  }
  
  @PutMapping("/memo")
  public ResponseEntity<Void> updateMemo(@RequestBody MemoRequest memoRequest) {
    dashboardService.updateMemo(memoRequest);
    return ResponseEntity.ok().build();
  }
  
  @DeleteMapping("/memo/{id}")
  public ResponseEntity<Void> deleteMemo(@PathVariable String id) {
    dashboardService.deleteMemo(id);
    return ResponseEntity.ok().build();
  }
}
