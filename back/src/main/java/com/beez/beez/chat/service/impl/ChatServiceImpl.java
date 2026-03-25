package com.beez.beez.chat.service.impl;

import com.beez.beez.chat.dto.ChatSendRequest;
import com.beez.beez.chat.dto.ChatVo;
import com.beez.beez.chat.mapper.ChatMapper;
import com.beez.beez.chat.repository.Chat;
import com.beez.beez.chat.repository.ChatRepository;
import com.beez.beez.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {
  private final ChatRepository chatRepository;
  private final ChatMapper chatMapper;
  
  @Override
  public void sendChat(String projectId, ChatSendRequest chat) {
    ChatVo vo = new ChatVo(projectId, chat);
    chatMapper.insertChat(vo);
  }
  
  @Override
  public List<ChatVo> findChatList(String projectId) {
    return chatMapper.findChatList(projectId);
  }
}
