package com.beez.beez.wiki.web;

import com.beez.beez.project.dto.ProjectCreateRequest;
import com.beez.beez.wiki.dto.WikiProjectRequest;
import com.beez.beez.wiki.dto.WikiRequest;
import com.beez.beez.wiki.dto.WikiSaveRequest;
import com.beez.beez.wiki.dto.WikiVersionRequest;
import com.beez.beez.wiki.service.WikiService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//사용자의 요청 URL을 받는 파일
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class WikiController {
  
  private final WikiService wikiService;
  
  // 위키 생성
  @PostMapping("/wiki/insert")
  public void insertWiki(@RequestBody WikiSaveRequest wikiSaveRequest){
    wikiService.insertWiki(
      wikiSaveRequest.getWikiRequest(),
      wikiSaveRequest.getVersionRequest()
    );
  }
  
  //위키 내용 수정
  @PostMapping("/wiki/update")
  public void updateWikiContent(@RequestBody WikiSaveRequest wikiSaveRequest){
    wikiService.updateWikiContent(wikiSaveRequest.getVersionRequest(), wikiSaveRequest.getWikiRequest());
  }
  
  //프로젝트별 위키 목록 조회
  @GetMapping("/wiki/list/{projectId}")
  public List<WikiRequest> findWikiByProjectId(@PathVariable String projectId) {
    return wikiService.findWikiByProjectId(projectId);
  }
  
  //특정 위키의 최신 버전 상세 조회
  @GetMapping("/wiki/detail/{wikiId}")
  public WikiVersionRequest findLatestVersion(@PathVariable String wikiId) {
    return wikiService.findLatestVersion(wikiId);
  }
  
  //위키 히스토리 조회
  @GetMapping("/wiki/history/{wikiId}")
  public List<WikiVersionRequest> findWikiVersionList(@PathVariable String wikiId) {
    return wikiService.findWikiVersionList(wikiId);
  }

  //위키에 프로젝트 정보 출력
  @GetMapping("/wiki/project/{projectId}")
  public WikiProjectRequest findProjectById(@PathVariable String projectId){
    return wikiService.getProjectInfo(projectId);
  }
}
