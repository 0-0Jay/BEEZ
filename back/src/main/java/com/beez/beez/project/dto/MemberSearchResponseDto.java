package com.beez.beez.project.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class MemberSearchResponseDto {
  private List<UserResponse> users;
  private List<GroupResponse> groups;
}
