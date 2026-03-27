package com.beez.beez.websocket.service.impl;

import com.beez.beez.websocket.dto.NotificationRequest;
import com.beez.beez.websocket.dto.NotificationResponse;
import com.beez.beez.websocket.service.NotificationService;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class NotificationServiceImpl implements NotificationService {
  @Override
  public void sendNotification(NotificationRequest notificationRequest) {
  
  }
  
  @Override
  public List<NotificationResponse> findNotificationList(String user) {
    return List.of();
  }
  
  @Override
  public void readNotification(String id) {
  
  }
}
