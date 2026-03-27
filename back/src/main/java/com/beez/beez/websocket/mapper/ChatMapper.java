package com.beez.beez.websocket.mapper;

import com.beez.beez.websocket.dto.ChatVo;

import java.util.List;

public interface ChatMapper {
  
  // 채팅 조회
  List<ChatVo> findChatList(String projectId);
}
