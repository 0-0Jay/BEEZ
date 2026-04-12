package com.beez.beez.logs.mapper;

import com.beez.beez.logs.dto.LogFilterRequest;
import com.beez.beez.logs.dto.LogListResponse;
import com.beez.beez.logs.dto.LogsCreateRequest;
import com.beez.beez.project.dto.ProjectFilterRequest;
import com.beez.beez.project.dto.ProjectListResponse;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface LogsMapper {
  //로그 생성
  void insertLogs(LogsCreateRequest dto);

  //로그 목록 조회
  List<LogListResponse> findLogList(LogFilterRequest filter);
  int countLogList(LogFilterRequest filter);
}
