package com.beez.beez.notification.repository;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {
  @Id
  private String id;
  private String userId;
  private String content;
  private LocalDateTime createdOn;
  private String status;
  private String link;
}