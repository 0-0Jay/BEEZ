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
  
  @GetMapping("/{id}/{userId}")
  public ResponseEntity<List<CalendarResponse>> findAllSchedule(@PathVariable String id, @PathVariable String userId) {
    return ResponseEntity.ok(calendarService.findAllSchedule(id, userId));
  }
  
  @PostMapping()
  public ResponseEntity<Void> insertSchedule(@RequestBody CalendarRequest calendarRequest) {
    calendarService.insertSchedule(calendarRequest);
    return ResponseEntity.ok().build();
  }
  
  @PutMapping()
  public ResponseEntity<Void> updateSchedule(@RequestBody CalendarRequest calendarRequest) {
    calendarService.updateSchedule(calendarRequest);
    return ResponseEntity.ok().build();
  }
  
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteSchedule(@PathVariable String id) {
    calendarService.deleteSchedule(id);
    return ResponseEntity.ok().build();
  }
}
