package com.beez.beez.notification.dto;

import com.beez.beez.notification.repository.Notification;
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
  private String userId;
  private String content;
  private String status;
  private LocalDateTime createdOn;
  private String link;
  
  public static NotificationResponse toDto(Notification notification) {
    return NotificationResponse.builder()
      .id(notification.getId())
      .userId(notification.getUserId())
      .content(notification.getContent())
      .status(notification.getStatus())
      .createdOn(notification.getCreatedOn())
      .link(notification.getLink())
      .build();
  }
}
