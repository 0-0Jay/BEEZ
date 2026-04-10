package com.beez.beez.calendar.web;

import com.beez.beez.calendar.dto.CalendarRequest;
import com.beez.beez.calendar.dto.CalendarResponse;
import com.beez.beez.calendar.service.CalendarService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/calendar")
public class CalendarController {
  private final CalendarService calendarService;
  
  @GetMapping("/{id}")
  public ResponseEntity<List<CalendarResponse>> findAllSchedule(@PathVariable String id) {
    return ResponseEntity.ok(calendarService.findAllSchedule(id));
  }
  
  @PostMapping()
  public ResponseEntity<Void> insertSchedule(@RequestBody CalendarRequest calendarRequest) {
    return ResponseEntity.ok().build();
  }
}
