package com.beez.beez.websocket.service;

import com.beez.beez.websocket.dto.NotificationRequest;
import com.beez.beez.websocket.dto.NotificationResponse;

import java.util.List;

public interface NotificationService {
  // 알림 전송
  void sendNotification(NotificationRequest notificationRequest);
  
  // 알림 목록
  List<NotificationResponse> findNotificationList(String user);
  
  // 알림 읽음
  void readNotification(String id);
  
  // 알림 전부 읽음
  void readAllNotification(String userId);
  
  void deleteNotification(String id);
}
