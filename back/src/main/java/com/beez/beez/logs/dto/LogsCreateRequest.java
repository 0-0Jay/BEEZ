package com.beez.beez.logs.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class LogsCreateRequest {
  private String logId;
  private String projectId;
  private String type;
  private String category;
  private String content;
  private Date createdOn;
  private String link;
}
