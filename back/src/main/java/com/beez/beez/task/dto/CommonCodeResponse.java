package com.beez.beez.task.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommonCodeResponse {
  private String cgroup;
  private String id;
  private String name;
}
