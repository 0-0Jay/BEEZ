package com.beez.beez.gits.dto;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class GitCommitRequest {

  private String commitSha;
  private String repoId;
  private String taskId;
  private String message;
  private String author;
  private Timestamp commitDate;
}
