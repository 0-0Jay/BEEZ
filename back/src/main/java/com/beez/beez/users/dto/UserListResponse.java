package com.beez.beez.users.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
@Data
public class UserListResponse {
  private String id;
  private String email;
  private String name;
  private String status;
  @JsonFormat(pattern = "yyyy-MM-dd")
  private LocalDateTime createdOn;
  private String role;
}
