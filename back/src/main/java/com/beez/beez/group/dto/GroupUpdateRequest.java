package com.beez.beez.group.dto;

import lombok.Data;

import java.util.List;

@Data
public class GroupUpdateRequest {

  private String id;
  private String name;
  private String description;
  private List<String> userIds;
  private List<GroupProjectDto> projects;
}
