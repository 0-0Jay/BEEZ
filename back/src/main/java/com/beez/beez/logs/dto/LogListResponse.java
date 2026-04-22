package com.beez.beez.logs.dto;

import lombok.Data;

import java.util.Date;

@Data
public class LogListResponse {
  private String logId;
  private String projectId;
  private String type;
  private String typeName;
  private String category;
  private String categoryName;
  private String content;
  private Date createdOn;
  private String link;
  private String userId;
  private String name;
}
