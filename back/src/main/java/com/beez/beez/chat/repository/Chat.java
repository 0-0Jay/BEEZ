package com.beez.beez.chat.repository;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
public class Chat {
  @Id
  private String id;
  
  @Column(name = "project_id")
  private String projectId;
  
  @Column(name = "user_id")
  private String userId;
  
  private String content;
  
  @Column(name = "created_on")
  private LocalDateTime createdOn;
  
  @Column(name = "parent_id")
  private String parentId;
  
  @Builder
  public Chat(String id, String projectId, String userId, String content, LocalDateTime createdOn, String parentId) {
    this.id = id;
    this.projectId = projectId;
    this.userId = userId;
    this.content = content;
    this.createdOn = createdOn;
    this.parentId = parentId;
  }
}
