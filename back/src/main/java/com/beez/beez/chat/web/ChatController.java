package com.beez.beez.chat.web;

import com.beez.beez.chat.dto.ChatSendRequest;
import com.beez.beez.chat.dto.ChatVo;
import com.beez.beez.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ChatController {
  private final ChatService chatService;
  
  @MessageMapping("/chat/{projectId}")
  @SendTo("/chat/{projectId}")
  public ChatSendRequest sendChat(@DestinationVariable String projectId, ChatSendRequest chat) {
    chatService.sendChat(projectId, chat);
    return chat;
  }
  
  @GetMapping("/chat/{projectId}")
  public List<ChatVo> getChats(@PathVariable String projectId) {
    return chatService.findChatList(projectId);
  }
}
