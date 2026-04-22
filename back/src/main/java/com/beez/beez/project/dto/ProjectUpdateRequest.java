package com.beez.beez.project.dto;

import lombok.Data;

import java.util.Date;

@Data
public class ProjectUpdateRequest {
  private String id;
  private String identifier;
  private String title;
  private String description;
  private String isPublic;
  private String parentId;
  private Date startDate;
  private Date endDate;
  private String defaultVersionId;
  private String pmId;
  private String userId;
}
