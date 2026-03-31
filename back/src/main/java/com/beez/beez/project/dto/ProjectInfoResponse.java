package com.beez.beez.project.dto;

import lombok.Data;

import java.util.Date;

@Data
public class ProjectInfoResponse {
  private String identifier;
  private String title;
  private String description;
  private String isPublic;
  private String parentId;
  private Date startDate;
  private Date endDate;
}
