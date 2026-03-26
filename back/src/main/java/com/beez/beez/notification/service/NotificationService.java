package com.beez.beez.notification.service;

import com.beez.beez.notification.dto.NotificationRequest;
import com.beez.beez.notification.dto.NotificationResponse;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

public interface NotificationService {
  SseEmitter subscribe(String userId);
  
  NotificationResponse sendToUser(NotificationRequest request);
  
  void broadcast(NotificationRequest request);
  
  List<NotificationResponse> getNotifications(String userId);
  
  void deleteNotification(Long id);
  
  void deleteAllByUserId(String userId);
  
  void markAsRead(String id);
  
  void markAllAsRead(String userId);
  
  Long countUnread(String userId);
}
