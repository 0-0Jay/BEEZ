package com.beez.beez.websocket.web;

import com.beez.beez.websocket.dto.ChatSendRequest;
import com.beez.beez.websocket.dto.ChatVo;
import com.beez.beez.websocket.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ChatController {
  private final ChatService chatService;
  
  // 채팅 전송
  @MessageMapping("/chat/{projectId}")
  @SendTo("/chat/{projectId}")
  public ResponseEntity<ChatSendRequest> sendChat(@DestinationVariable String projectId, ChatSendRequest chat) {
    chatService.sendChat(projectId, chat);
    return ResponseEntity.ok(chat);
  }
  
  // 채팅 목록
  @GetMapping("/api/chat/{projectId}")
  public ResponseEntity<List<ChatVo>> getChats(@PathVariable String projectId) {
    return ResponseEntity.ok(chatService.findChatList(projectId));
  }
}
