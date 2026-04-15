package com.beez.beez.project.dto;

import lombok.Data;

import java.util.Date;

@Data
public class ProjectCopyRequest {
  private String sourceProjectId;
  private String identifier;
  private String title;
  private String description;
  private String isPublic;
  private String parentId;
  private Date startDate;
  private Date endDate;
  private String userId;
  private String copyMembers;
  private String copyVersions;
  private String copyIssues;
  private String copyDocs;
  private String copyWiki;
  private String newProjectId;  // OUT 파라미터, 프로시저 실행 후 여기에 담김
}
