package com.beez.beez.task.web;

import com.beez.beez.task.dto.TaskSpentResponse;
import com.beez.beez.task.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/spent")
@RestController
public class SpentController {
  private final TaskService taskService;
  
  // 소요시간
  @GetMapping("/{id}")
  public ResponseEntity<List<TaskSpentResponse>> findSpentOverview(@PathVariable String id) {
    return ResponseEntity.ok(taskService.findSpentOverview(id));
  }
}
