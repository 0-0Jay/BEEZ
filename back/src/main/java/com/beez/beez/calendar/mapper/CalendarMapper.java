package com.beez.beez.calendar.mapper;

import com.beez.beez.calendar.dto.CalendarRequest;
import com.beez.beez.calendar.dto.CalendarResponse;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CalendarMapper {
  void insertSchedule(CalendarRequest calendarRequest);
  
  List<CalendarResponse> findAllSchedule(@Param("id") String id, @Param("userId") String userId);
  
  void updateSchedule(CalendarRequest calendarRequest);
  
  void deleteSchedule(String id);
}
