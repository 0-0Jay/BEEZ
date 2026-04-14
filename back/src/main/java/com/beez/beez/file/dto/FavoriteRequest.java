package com.beez.beez.file.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteRequest { //즐겨찾기 기능 위한 DTO파일
  private String markId;
  private String userId;
  private String documentId;
}
