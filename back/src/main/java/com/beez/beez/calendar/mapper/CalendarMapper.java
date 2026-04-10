package com.beez.beez.calendar.mapper;

import com.beez.beez.calendar.dto.CalendarRequest;
import com.beez.beez.calendar.dto.CalendarResponse;

import java.util.List;

public interface CalendarMapper {
  void insertSchedule(CalendarRequest calendarRequest);
  
  List<CalendarResponse> findAllSchedule(String id);
}
