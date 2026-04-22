package com.beez.beez.project.dto;

import lombok.Data;

import java.util.Date;

@Data
public class ProjectListResponse {
  private String id;
  private String title;
  private String identifier;
  private String creator;
  private String creatorName;
  private String pmId;
  private String pmName;
  private Date startDate;
  private Date endDate;
  private String isLock;
  private int level; // 계층 깊이
  private String parentId;

  private Integer totalTaskCount;
  private Integer completedTaskCount;
  private Integer progressRate;

}
