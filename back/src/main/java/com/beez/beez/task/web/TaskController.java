package com.beez.beez.task.web;

import com.beez.beez.task.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TaskController {
  private final TaskService taskService;
  
  
}
