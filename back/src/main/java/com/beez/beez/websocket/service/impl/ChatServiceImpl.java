package com.beez.beez.websocket.service.impl;

import com.beez.beez.websocket.dto.ChatSendRequest;
import com.beez.beez.websocket.dto.ChatVo;
import com.beez.beez.websocket.mapper.ChatMapper;
import com.beez.beez.websocket.repository.ChatRepository;
import com.beez.beez.websocket.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {
  private final ChatRepository chatRepository;
  private final ChatMapper chatMapper;
  
  // 채팅 전송
  @Override
  public void sendChat(String projectId, ChatSendRequest chat) {
    chatRepository.insertChat(projectId, chat.getUserId(), chat.getContent(), chat.getParentId());
  }
  
  // 채팅 목록
  @Override
  public List<ChatVo> findChatList(String projectId) {
    return chatMapper.findChatList(projectId);
  }
}
