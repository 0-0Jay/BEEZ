package com.beez.beez.file.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ListResponse {
  private String id;
  private String userId;
  private String title;
  private String doctype;
  private String userName;
  private LocalDateTime createdOn;
  private String isMarked;
}
