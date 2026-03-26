package com.beez.beez.notification.web;

import com.beez.beez.notification.dto.NotificationRequest;
import com.beez.beez.notification.dto.NotificationResponse;
import com.beez.beez.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // 실제 운영 시 allowedOrigins를 명시하세요
public class NotificationController {
  
  private final NotificationService notificationService;
  
  /**
   * SSE 구독 엔드포인트
   * - 프론트에서 EventSource로 연결
   * GET /api/notifications/subscribe/{userId}
   */
  @GetMapping(value = "/subscribe/{userId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public SseEmitter subscribe(@PathVariable String userId) {
    return notificationService.subscribe(userId);
  }
  
  /**
   * 특정 유저에게 알림 전송 (DB 저장 + SSE 실시간 전송)
   * POST /api/notifications/send
   */
  @PostMapping("/send")
  public NotificationResponse sendNotification(
    @RequestBody NotificationRequest request
  ) {
    return notificationService.sendToUser(request);
  }
  
  /**
   * 전체 유저에게 브로드캐스트
   * POST /api/notifications/broadcast
   */
  @PostMapping("/broadcast")
  public void broadcast(@RequestBody NotificationRequest request) {
    notificationService.broadcast(request);
  }
  
  /**
   * 특정 유저의 알림 목록 조회
   * GET /api/notifications/{userId}
   */
  @GetMapping("/{userId}")
  public List<NotificationResponse> getNotifications(
    @PathVariable String userId
  ) {
    return notificationService.getNotifications(userId);
  }
  
  /**
   * 알림 단건 삭제
   * DELETE /api/notifications/{id}
   */
  @DeleteMapping("/{id}")
  public void deleteNotification(@PathVariable Long id) {
    notificationService.deleteNotification(id);
  }
  
  /**
   * 특정 유저의 알림 전체 삭제
   * DELETE /api/notifications/all/{userId}
   */
  @DeleteMapping("/all/{userId}")
  public void deleteAllNotifications(@PathVariable String userId) {
    notificationService.deleteAllByUserId(userId);
  }
  
  /**
   * 알림 단건 읽음 처리
   * PATCH /api/notifications/{id}/read
   */
  @PatchMapping("/{id}/read")
  public void markAsRead(@PathVariable String id) {
    notificationService.markAsRead(id);
  }
  
  /**
   * 특정 유저의 모든 알림 읽음 처리
   * PATCH /api/notifications/read-all/{userId}
   */
  @PatchMapping("/read-all/{userId}")
  public void markAllAsRead(@PathVariable String userId) {
    notificationService.markAllAsRead(userId);
  }
  
  /**
   * 읽지 않은 알림 수 조회
   * GET /api/notifications/{userId}/unread-count
   */
  @GetMapping("/{userId}/unread-count")
  public Long getUnreadCount(@PathVariable String userId) {
    return notificationService.countUnread(userId);
  }
}
