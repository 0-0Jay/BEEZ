package com.beez.beez.notification.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationRequest {
  private String userId;
  private String content;
  private String link;
}
