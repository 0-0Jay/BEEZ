package com.beez.beez.group.dto;

import lombok.Data;

import java.util.List;

@Data
public class GroupProjectDto {
  private String projectId;
  private List<String> roleIds;
}
