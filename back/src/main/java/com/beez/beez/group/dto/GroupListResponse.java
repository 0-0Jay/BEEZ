package com.beez.beez.group.dto;

import lombok.Data;

@Data
public class GroupListResponse {

  private String id;
  private String name;
  private String description;
  private int memberCnt;
  private int projectCnt;
}
