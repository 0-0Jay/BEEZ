package com.beez.beez.websocket.service;

import com.beez.beez.websocket.dto.ChatSendRequest;
import com.beez.beez.websocket.dto.ChatVo;

import java.util.List;

public interface ChatService {
  public void sendChat(String projectId, ChatSendRequest chat);
  
  public List<ChatVo> findChatList(String projectId);
}
