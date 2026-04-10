package com.beez.beez.calendar.service.impl;

import com.beez.beez.calendar.dto.CalendarRequest;
import com.beez.beez.calendar.dto.CalendarResponse;
import com.beez.beez.calendar.mapper.CalendarMapper;
import com.beez.beez.calendar.service.CalendarService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CalendarServiceImpl implements CalendarService {
  private final CalendarMapper calendarMapper;
  
  // 일정 추가
  @Override
  public void insertSchedule(CalendarRequest calendarRequest) {
    calendarMapper.insertSchedule(calendarRequest);
  }
  
  // 프로젝트 일정
  @Override
  public List<CalendarResponse> findAllSchedule(String id) {
    return calendarMapper.findAllSchedule(id);
  }
}
