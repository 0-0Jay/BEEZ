package com.beez.beez.chat.service;

import com.beez.beez.chat.dto.ChatSendRequest;
import com.beez.beez.chat.dto.ChatVo;
import com.beez.beez.chat.repository.Chat;

import java.util.List;

public interface ChatService {
  public void sendChat(String projectId, ChatSendRequest chat);
  
  public List<ChatVo> findChatList(String projectId);
}
