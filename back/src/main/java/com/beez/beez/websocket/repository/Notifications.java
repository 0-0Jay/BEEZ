package com.beez.beez.websocket.repository;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Notifications {
  @Id
  String id;
  String userId;
  String content;
  LocalDateTime createdOn;
  String status;
  String link;
}
