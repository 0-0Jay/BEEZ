package com.beez.beez.notification.service.sse;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class SseEmitterManager {
  
  // userId -> SseEmitter 매핑
  private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();
  // SSE 타임아웃 (30분)
  private static final long SSE_TIMEOUT = 30 * 60 * 1000L;
  
  // 새로운 SSE 연결 등록
  public SseEmitter add(String userId) {
    SseEmitter emitter = new SseEmitter(SSE_TIMEOUT);
    
    // 연결 완료, 타임아웃, 에러 시 자동 제거
    emitter.onCompletion(() -> {
      emitters.remove(userId);
    });
    emitter.onTimeout(() -> {
      emitter.complete();
      emitters.remove(userId);
    });
    emitter.onError((e) -> {
      emitters.remove(userId);
    });
    
    // SSE 구독
    emitters.put(userId, emitter);
    
    // 연결 확인용 초기 이벤트 전송 (keep-alive)
    sendEvent(userId, "connect", "connected");
    return emitter;
  }
  
  // 알림 전송
  public void sendEvent(String userId, String eventName, Object data) {
    SseEmitter emitter = emitters.get(userId);
    if (emitter == null) {
      return;
    }
    try {
      emitter.send(
        SseEmitter.event()
          .name(eventName)
          .data(data)
      );
    } catch (IOException e) {
      emitters.remove(userId);
    }
  }
  
  // 알림 브로드캐스트
  public void broadcast(String eventName, Object data) {
    emitters.forEach((userId, emitter) -> sendEvent(userId, eventName, data));
  }
  
  // 특정 유저 구독 해제
  public void remove(String userId) {
    SseEmitter emitter = emitters.remove(userId);
    if (emitter != null) {
      emitter.complete();
    }
  }
  
  // 연결 사용자 수 조회
  public int getConnectionCount() {
    return emitters.size();
  }
  
  // 특저 사용자 연결 여부 조회
  public boolean isConnected(String userId) {
    return emitters.containsKey(userId);
  }
}