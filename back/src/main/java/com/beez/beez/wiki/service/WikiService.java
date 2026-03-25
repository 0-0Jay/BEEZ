package com.beez.beez.wiki.service;

import com.beez.beez.wiki.dto.WikiRequest;
import com.beez.beez.wiki.dto.WikiVersionRequest;

import java.util.List;

//핵심 로직이 들어가는 파일 - 어떤 기능이 필요한지 이름만 정의 (실제 구현 코드는 impl파일에서 작성)
public interface WikiService {
  
  //신규 위키 등록
  void createWiki (WikiRequest wikiRequest, WikiVersionRequest wikiVersionRequest);
  
  //프로젝트별 위키 목록 조회
  List<WikiRequest> getWikiList(String projectId);
  
  //위키 상세 내용 조회
  WikiVersionRequest getWikiDetail(String wikiId);
}
