package com.beez.beez.websocket.service.impl;

import com.beez.beez.websocket.dto.NotificationRequest;
import com.beez.beez.websocket.dto.NotificationResponse;
import com.beez.beez.websocket.mapper.NotificationMapper;
import com.beez.beez.websocket.repository.NotificationRepository;
import com.beez.beez.websocket.service.NotificationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
  private final NotificationRepository notificationRepository;
  private final NotificationMapper notificationMapper;
  private final SimpMessagingTemplate messagingTemplate;
  
  // 알림 전송
  @Override
  @Transactional
  public void sendNotification(NotificationRequest dto) {
    notificationRepository.insertNotification(dto.getUserId(), dto.getContent(), dto.getLink(), dto.getProjectId());
    NotificationResponse notificationResponse = notificationMapper.findNotification(dto.getUserId());
    System.out.println(dto.getUserId() + " , " + notificationResponse.getContent());
    messagingTemplate.convertAndSend("/notification/" + dto.getUserId(), notificationResponse);
  }
  
  // 알림 목록
  @Override
  public List<NotificationResponse> findNotificationList(String userId) {
    return notificationMapper.findAllNotification(userId);
  }
  
  // 알림 읽음
  @Override
  public void readNotification(String id) {
    notificationMapper.readNotification(id);
  }
  
  // 알림 전부 읽음
  @Override
  public void readAllNotification(String userId) {
    notificationMapper.readAllNotification(userId);
  }
  
  // 알림 삭제
  @Override
  public void deleteNotification(String id) {
    notificationRepository.deleteById(id);
  }
}
