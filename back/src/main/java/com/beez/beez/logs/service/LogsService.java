package com.beez.beez.logs.service;

import com.beez.beez.logs.dto.LogFilterRequest;
import com.beez.beez.logs.dto.LogListResponse;
import com.beez.beez.logs.dto.LogsCreateRequest;

import java.util.List;
import java.util.Map;

public interface LogsService {
  //로그 작성
  void insertLogs(String projectId, String type, String category, String content, String link, String userId);

  //로그 목록 조회
  Map<String, Object> findLogList(LogFilterRequest filter);
}