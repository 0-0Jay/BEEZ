package com.beez.beez.wiki.dto;

import lombok.Data;

@Data
public class WikiSaveRequest {
  private WikiRequest wikiRequest;
  private WikiVersionRequest versionRequest;
}
