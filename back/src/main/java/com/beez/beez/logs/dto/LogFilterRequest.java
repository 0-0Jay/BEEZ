package com.beez.beez.logs.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class LogFilterRequest {
  private String projectId;
  private String type;
  private String category;
  private String userId;
  @DateTimeFormat(pattern = "yyyy-MM-dd")
  private Date startDate;
  @DateTimeFormat(pattern = "yyyy-MM-dd")
  private Date endDate;
  private String content;

  private int page;
  private int size;
  private int offset;
}
