package com.beez.beez.logs.service.impl;

import com.beez.beez.logs.dto.LogFilterRequest;
import com.beez.beez.logs.dto.LogListResponse;
import com.beez.beez.logs.dto.LogsCreateRequest;
import com.beez.beez.logs.mapper.LogsMapper;
import com.beez.beez.logs.service.LogsService;
import com.beez.beez.project.dto.ProjectFilterRequest;
import com.beez.beez.project.dto.ProjectListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Transactional
@RequiredArgsConstructor
@Service
public class LogsServiceImpl implements LogsService {
  
  private final LogsMapper logsMapper;
  
  @Override
  @Transactional(propagation = Propagation.REQUIRES_NEW)
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

  //로그 목록 조회(필터링)
  @Override
  public Map<String, Object> findLogList(LogFilterRequest filter) {
    if (filter.getPage() == 0) filter.setPage(1);
    if (filter.getSize() == 0) filter.setSize(10);
    filter.setOffset((filter.getPage() - 1) * filter.getSize());

    List<LogListResponse> list = logsMapper.findLogList(filter);
    int total = logsMapper.countLogList(filter);

    Map<String, Object> result = new HashMap<>();
    result.put("list", list);
    result.put("total", total);
    return result;
  }
  
}
