package com.beez.beez.calendar.service;


import com.beez.beez.calendar.dto.CalendarRequest;
import com.beez.beez.calendar.dto.CalendarResponse;

import java.util.List;

public interface CalendarService {
  // 일정 추가
  void insertSchedule(CalendarRequest calendarRequest);
  
  // 프로젝트 일정
  List<CalendarResponse> findAllSchedule(String id);
}
