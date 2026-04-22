package com.beez.beez.task.web;

import com.beez.beez.task.dto.GanttDataResponse;
import com.beez.beez.task.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/gantt")
public class GanttController {
  
  private final TaskService taskService;
  
  @GetMapping("/{id}")
  public ResponseEntity<List<GanttDataResponse>> findGanttData(@PathVariable String id) {
    return ResponseEntity.ok(taskService.findGanttData(id));
  }
  
}
