package com.beez.beez.wiki.dto;

import lombok.Data;

@Data
//작성하면 한번에 처리 하기 위함
public class WikiSaveRequest {
  private WikiRequest wikiRequest;
  private WikiVersionRequest versionRequest;
}
