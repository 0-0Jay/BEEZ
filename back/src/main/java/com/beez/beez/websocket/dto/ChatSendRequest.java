package com.beez.beez.websocket.dto;

import lombok.Data;

@Data
public class ChatSendRequest {
  private String userId;
  private String content;
  private String parentId;
}
