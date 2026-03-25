package com.beez.beez.chat.mapper;

import com.beez.beez.chat.dto.ChatVo;

import java.util.List;

public interface ChatMapper {
  
  // 채팅 저장
  void insertChat(ChatVo chatVo);
  
  // 채팅 조회
  List<ChatVo> findChatList(String projectId);
}
