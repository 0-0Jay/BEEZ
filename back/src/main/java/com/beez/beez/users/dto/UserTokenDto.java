package com.beez.beez.users.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserTokenDto {

  private String tokenId;
  private String userId;
  private String tokenValue;
  private String tokenType;
  private LocalDateTime expiryDate;
  private LocalDateTime createdOn;
}
