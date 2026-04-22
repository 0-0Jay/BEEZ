package com.beez.beez.websocket.service;

import com.beez.beez.websocket.dto.ChatSendRequest;
import com.beez.beez.websocket.dto.ChatVo;

import java.util.List;

public interface ChatService {
  // 채팅 전송
  public void sendChat(String projectId, ChatSendRequest chat);
  
  // 채팅 목록
  public List<ChatVo> findChatList(String projectId);
}
