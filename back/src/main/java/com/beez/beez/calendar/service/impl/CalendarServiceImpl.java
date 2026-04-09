package com.beez.beez.calendar.service.impl;

import com.beez.beez.calendar.mapper.CalendarMapper;
import com.beez.beez.calendar.service.CalendarService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CalendarServiceImpl implements CalendarService {
  private final CalendarMapper calendarMapper;
}
