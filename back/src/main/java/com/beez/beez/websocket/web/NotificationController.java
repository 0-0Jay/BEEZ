package com.beez.beez.websocket.web;

import com.beez.beez.websocket.dto.NotificationRequest;
import com.beez.beez.websocket.dto.NotificationResponse;
import com.beez.beez.websocket.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class NotificationController {
  private final NotificationService notificationService;
  
  // 알림 전송
  @MessageMapping("/notification/{userId}")
  @SendTo("/notification/{userId}")
  public ResponseEntity<NotificationRequest> sendNotification(@DestinationVariable String userId, NotificationRequest notification) {
    notificationService.sendNotification(notification);
    return ResponseEntity.ok(notification);
  }
  
  // 알림 목록
  @GetMapping("/api/notification/{userId}")
  public ResponseEntity<List<NotificationResponse>> getNotifications(@PathVariable String userId) {
    return ResponseEntity.ok(notificationService.findNotificationList(userId));
  }
  
  // 알림 읽음
  @PutMapping("/api/notification/{id}")
  public ResponseEntity<Void> readNotification(@PathVariable String id) {
    notificationService.readNotification(id);
    return ResponseEntity.ok().build();
  }
  
  // 알림 전부 읽음
  @PutMapping("/api/notification/{userId}/all")
  public ResponseEntity<Void> readAllNotification(@PathVariable String userId) {
    notificationService.readAllNotification(userId);
    return ResponseEntity.ok().build();
  }
  
  // 알림 삭제
  @DeleteMapping("/api/notification/{id}")
  public ResponseEntity<Void> deleteNotification(@PathVariable String id) {
    notificationService.deleteNotification(id);
    return ResponseEntity.ok().build();
  }
  
  // 알림 전송 테스트
  @PostMapping("/api/notification/test/{userId}")
  public ResponseEntity<Void> testNotification(@PathVariable String userId, @RequestBody NotificationRequest notificationRequest) {
    notificationService.sendNotification(notificationRequest);
    return ResponseEntity.ok().build();
  }
}
