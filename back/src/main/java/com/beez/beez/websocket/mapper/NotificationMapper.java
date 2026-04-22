package com.beez.beez.websocket.mapper;

import com.beez.beez.websocket.dto.NotificationResponse;

import java.util.List;

public interface NotificationMapper {
  void readNotification(String id);
  
  void readAllNotification(String userId);
  
  NotificationResponse findNotification(String userId);
  
  List<NotificationResponse> findAllNotification(String userId);
}
