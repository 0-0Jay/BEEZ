package com.beez.beez.chat.dto;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChatVo {
  private String id; // 자동생성
  private String projectId;
  private String userId;
  private String userName;  // 조인
  private String content;
  private LocalDateTime createdOn; // 자동생성
  private String parentId;
  
  public ChatVo(String projectId, ChatSendRequest dto) {
    this.projectId = projectId;
    this.userId = dto.getUserId();
    this.content = dto.getContent();
    this.parentId = dto.getParentId();
  }
}
