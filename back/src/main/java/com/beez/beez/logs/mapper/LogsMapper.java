package com.beez.beez.logs.mapper;

import com.beez.beez.logs.dto.LogsCreateRequest;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LogsMapper {
  void insertLogs(LogsCreateRequest dto);
}
