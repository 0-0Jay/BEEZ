package com.beez.beez.notification.service.impl;

import com.beez.beez.notification.dto.NotificationRequest;
import com.beez.beez.notification.dto.NotificationResponse;
import com.beez.beez.notification.repository.Notifications;
import com.beez.beez.notification.repository.NotificationRepository;
import com.beez.beez.notification.service.NotificationService;
import com.beez.beez.notification.service.sse.SseEmitterManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
  private final NotificationRepository notificationRepository;
  private final SseEmitterManager sseEmitterManager;
  private final ObjectMapper objectMapper;
  
  /**
   * SSE 구독 (연결)
   * - 프론트에서 최초 1회 호출, 이후 서버 -> 클라이언트 push
   */
  public SseEmitter subscribe(String userId) {
    return sseEmitterManager.add(userId);
  }
  
  /**
   * 특정 유저에게 알림 전송 (DB 저장 + SSE 실시간 전송)
   */
  @Transactional
  public NotificationResponse sendToUser(NotificationRequest request) {
    // 1. DB 저장
    Notifications notification = Notifications.builder()
      .userId(request.getUserId())
      .content(request.getContent())
      .link(request.getLink())
      .build();
    notification = notificationRepository.save(notification);
    
    NotificationResponse dto = NotificationResponse.toDto(notification);
    
    // 2. SSE 실시간 전송
    try {
      String json = objectMapper.writeValueAsString(dto);
      sseEmitterManager.sendEvent(request.getUserId(), "notification", json);
    } catch (Exception e) {
    }
    
    return dto;
  }
  
  /**
   * 전체 유저에게 브로드캐스트 (DB 저장 없이 SSE만 전송)
   * - 필요에 따라 DB 저장 로직 추가 가능
   */
  public void broadcast(NotificationRequest request) {
    try {
      NotificationResponse dto = NotificationResponse.builder()
        .content(request.getContent())
        .link(request.getLink())
        .status("0")
        .build();
      String json = objectMapper.writeValueAsString(dto);
      sseEmitterManager.broadcast("notification", json);
    } catch (Exception e) {
    }
  }
  
  /**
   * 특정 유저의 알림 목록 조회
   */
  public List<NotificationResponse> getNotifications(String userId) {
    return notificationRepository.findByUserIdOrderByCreatedOnDesc(userId)
      .stream()
      .map(NotificationResponse::toDto)
      .collect(Collectors.toList());
  }
  
  @Override
  public void deleteNotification(Long id) {
  
  }
  
  /**
   * 알림 단건 삭제
   */
  @Transactional
  public void deleteNotification(String id) {
    notificationRepository.deleteById(id);
  }
  
  /**
   * 특정 유저의 알림 전체 삭제
   */
  @Transactional
  public void deleteAllByUserId(String userId) {
    notificationRepository.deleteAllByUserId(userId);
  }
  
  /**
   * 알림 읽음 처리
   */
  @Transactional
  public void markAsRead(String id) {
    Notifications notification = notificationRepository.findById(id)
      .orElseThrow(() -> new IllegalArgumentException("알림을 찾을 수 없습니다. id=" + id));
    notification.setStatus("1");
  }
  
  /**
   * 특정 유저의 모든 알림 읽음 처리
   */
  @Transactional
  public void markAllAsRead(String userId) {
    notificationRepository.markAllAsReadByUserId(userId);
  }
  
  /**
   * 읽지 않은 알림 수 조회
   */
  public Long countUnread(String userId) {
    return notificationRepository.countByUserIdAndReadFalse(userId);
  }
}
