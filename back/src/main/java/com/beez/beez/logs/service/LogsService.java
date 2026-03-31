package com.beez.beez.logs.service;

import com.beez.beez.logs.dto.LogsCreateRequest;

public interface LogsService {
  void insertLogs(String projectId, String type, String category, String content, String link, String userId);
}