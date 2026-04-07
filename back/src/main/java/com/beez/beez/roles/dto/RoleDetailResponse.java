package com.beez.beez.roles.dto;

import lombok.Data;

import java.util.List;

@Data
public class RoleDetailResponse {

  private String id;
  private String name;
  private String isAssignee;
  private List<String> perIds;

}

