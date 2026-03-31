package com.beez.beez.logs.service.impl;

import com.beez.beez.logs.dto.LogsCreateRequest;
import com.beez.beez.logs.mapper.LogsMapper;
import com.beez.beez.logs.service.LogsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@RequiredArgsConstructor
@Service
public class LogsServiceImpl implements LogsService {
  
  private final LogsMapper logsMapper;
  
  @Override
  public void insertLogs(String projectId, String type, String category, String content, String link, String userId) {
    LogsCreateRequest dto = LogsCreateRequest.builder()
      .projectId(projectId)
      .type(type)
      .category(category)
      .content(content)
      .link(link)
      .userId(userId)
      .build();
    
    logsMapper.insertLogs(dto);
  }
  
}
