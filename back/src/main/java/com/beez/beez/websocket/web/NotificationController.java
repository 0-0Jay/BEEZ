package com.beez.beez.websocket.web;

import com.beez.beez.websocket.dto.NotificationRequest;
import com.beez.beez.websocket.dto.NotificationResponse;
import com.beez.beez.websocket.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class NotificationController {
  private final NotificationService notificationService;
  
  // 알림 전송 (클라이언트가 /send/notification/{userId} 로 보낼 때)
  @MessageMapping("/notification/{userId}")
  @SendTo("/notification/{userId}")
  public NotificationRequest sendNotification(@DestinationVariable String userId, NotificationRequest notification) {
    notificationService.sendNotification(notification);
    return notification;
  }
  
  // 알림 목록 조회 (REST)
  @GetMapping("/api/notification/{userId}")
  public List<NotificationResponse> getNotifications(@PathVariable String userId) {
    return notificationService.findNotificationList(userId);
  }
  
  // 알림 읽음 처리 (REST)
  @PutMapping("/api/notification/{notificationId}")
  public void readNotification(@PathVariable String notificationId) {
    notificationService.readNotification(notificationId);
  }
}
