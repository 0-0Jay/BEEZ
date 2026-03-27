package com.beez.beez.websocket.service;

import com.beez.beez.websocket.dto.NotificationRequest;
import com.beez.beez.websocket.dto.NotificationResponse;

import java.util.List;

public interface NotificationService {
  // 알림 전송
  public void sendNotification(NotificationRequest notificationRequest);
  
  // 알림 목록
  public List<NotificationResponse> findNotificationList(String user);
  
  // 알림 읽음 처리
  public void readNotification(String id);
}
