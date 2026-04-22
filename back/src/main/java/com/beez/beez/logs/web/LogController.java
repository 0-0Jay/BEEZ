package com.beez.beez.logs.web;

import com.beez.beez.logs.dto.LogFilterRequest;
import com.beez.beez.logs.dto.LogListResponse;
import com.beez.beez.logs.service.LogsService;
import com.beez.beez.project.dto.ProjectFilterRequest;
import com.beez.beez.project.dto.ProjectListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/log")
public class LogController {

  private final LogsService logsService;

  //로그 목록 조회
  @GetMapping("/list")
  public ResponseEntity<Map<String, Object>> findLogList(@ModelAttribute LogFilterRequest filter) {
    return ResponseEntity.ok(logsService.findLogList(filter));
  }

}
