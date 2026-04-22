package com.beez.beez.task.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JournalDetailResponse {
  private String id;
  private String fieldName;
  private String oldValue;
  private String newValue;
}
