package com.beez.beez.websocket.dto;

import com.beez.beez.websocket.repository.Notifications;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationResponse {
  private String id;
  private String content;
  private LocalDateTime createdOn;
  private String status;
  private String link;
  private String projectId;
  private String title;
}
