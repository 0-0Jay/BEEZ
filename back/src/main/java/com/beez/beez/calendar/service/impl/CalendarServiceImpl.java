package com.beez.beez.calendar.service.impl;

import com.beez.beez.calendar.dto.CalendarRequest;
import com.beez.beez.calendar.dto.CalendarResponse;
import com.beez.beez.calendar.mapper.CalendarMapper;
import com.beez.beez.calendar.service.CalendarService;
import com.beez.beez.websocket.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CalendarServiceImpl implements CalendarService {
  private final CalendarMapper calendarMapper;
  private final NotificationService notificationService;
  
  // 일정 추가
  @Override
  public void insertSchedule(CalendarRequest calendarRequest) {
    calendarMapper.insertSchedule(calendarRequest);
    
    // 팀 일정이면 팀전원에게 알림
    // 팀원 목록
    
  }
  
  // 프로젝트 일정
  @Override
  public List<CalendarResponse> findAllSchedule(String id, String userId) {
    return calendarMapper.findAllSchedule(id, userId);
  }
  
  // 일정 수정
  @Override
  public void updateSchedule(CalendarRequest calendarRequest) {
    calendarMapper.updateSchedule(calendarRequest);
  }
  
  // 일정 삭제
  @Override
  public void deleteSchedule(String id) {
    calendarMapper.deleteSchedule(id);
  }
}
