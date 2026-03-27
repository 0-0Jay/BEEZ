package com.beez.beez.websocket.web;

import com.beez.beez.websocket.dto.NotificationRequest;
import com.beez.beez.websocket.dto.NotificationResponse;
import com.beez.beez.websocket.service.NotificationService;
import lombok.RequiredArgsConstructor;
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
  public NotificationRequest sendNotification(@DestinationVariable String userId, NotificationRequest notification) {
    notificationService.sendNotification(notification);
    return notification;
  }
  
  // 알림 목록
  @GetMapping("/api/notification/{userId}")
  public List<NotificationResponse> getNotifications(@PathVariable String userId) {
    return notificationService.findNotificationList(userId);
  }
  
  // 알림 읽음
  @PutMapping("/api/notification/{id}")
  public void readNotification(@PathVariable String id) {
    notificationService.readNotification(id);
  }
  
  // 알림 전부 읽음
  @PutMapping("/api/notification/{userId}/all")
  public void readAllNotification(@PathVariable String userId) {
    notificationService.readAllNotification(userId);
  }
  
  // 알림 삭제
  @DeleteMapping("/api/notification/{id}")
  public void deleteNotification(@PathVariable String id) {
    notificationService.deleteNotification(id);
  }
  
  // 알림 전송 테스트
  @PostMapping("/api/notification/test/{userId}")
  public void testNotification(@PathVariable String userId, @RequestBody NotificationRequest notificationRequest) {
    notificationService.sendNotification(notificationRequest);
  }
}
