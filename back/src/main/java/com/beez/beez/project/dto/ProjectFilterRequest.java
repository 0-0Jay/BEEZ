package com.beez.beez.project.dto;

import lombok.Data;

import java.util.Date;

@Data
public class ProjectFilterRequest {
  private String id;
  private String pmId;
  private Date startDate;
  private Date endDate;
  private String isLock;
}
