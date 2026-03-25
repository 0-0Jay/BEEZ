package com.beez.beez.wiki.web;

import com.beez.beez.wiki.dto.WikiRequest;
import com.beez.beez.wiki.dto.WikiVersionRequest;
import com.beez.beez.wiki.service.WikiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//사용자의 요청 URL을 받는 파일
@RestController
@RequestMapping
public class WikiController {

  @Autowired
  private WikiService wikiService;
  
//  // 1. 위키 생성 요청 처리
//  @PostMapping("")
//  public String createWiki(@RequestBody WikiRequest wikiRequest,
//                           WikiVersionRequest wikiVersionRequest) {
//    // 현우님이 만든 서비스의 createWiki 수행!
//    wikiService.createWiki(wikiRequest, wikiVersionRequest);
//    return "성공적으로 등록되었습니다.";
//  }
  
  // 2. 프로젝트별 위키 목록 조회
  @GetMapping("/wiki/list/{projectId}")
  public List<WikiRequest> getWikiList(@PathVariable String projectId) {
    return wikiService.getWikiList(projectId);
  }
  
  // 3. 위키 상세 정보 조회
  @GetMapping("/wiki/detail/{wikiId}")
  public WikiVersionRequest getWikiDetail(@PathVariable String wikiId) {
    return wikiService.getWikiDetail(wikiId);
  }
  
}
