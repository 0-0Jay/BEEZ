package com.beez.beez.wiki.service.impl;

import com.beez.beez.project.dto.ProjectCreateRequest;
import com.beez.beez.wiki.dto.WikiRequest;
import com.beez.beez.wiki.dto.WikiVersionRequest;
import com.beez.beez.wiki.mapper.WikiMapper;
import com.beez.beez.wiki.service.WikiService;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// 로직 구현체 파일 - 실제로 어떻게 동작하는지 코드 작성
@Service
@RequiredArgsConstructor
public class WikiServiceImpl implements WikiService {
  
  private final WikiMapper wikiMapper;
  
  @Override
  @Transactional // 생성시 세가지 작업 모두 수행 -
  public void insertWiki(WikiRequest wikiRequest, WikiVersionRequest versionRequest){
    System.out.println(wikiRequest.getId());
    System.out.println(versionRequest.getVersionId());
    wikiMapper.insertWiki(wikiRequest);
    wikiMapper.insertWikiVersion(versionRequest);
    wikiMapper.updateWiki(wikiRequest);
  }
  
  @Override
  @Transactional // 수정시 본문 추가랑 wiki테이블 최신 버전 갱신
  public void updateWikiContent(WikiVersionRequest wikiVersionRequest, WikiRequest wikiRequest){
    wikiMapper.insertWikiVersion(wikiVersionRequest);
    wikiMapper.updateWiki(wikiRequest);
  }
  
  @Override //projectId별로 위키목록 조회
  public List<WikiRequest> findWikiByProjectId(String projectId){
    return wikiMapper.findWikiByProjectId(projectId);
  }
  
  @Override // wikiId별로 최신 본문 조회
  public WikiVersionRequest findLatestVersion(String wikiId){
    return wikiMapper.findLatestVersion(wikiId);
  }
  
  @Override //wikiId별로 히스토리 조회
  public List<WikiVersionRequest> findWikiVersionList(String wikiId){
    return wikiMapper.findWikiVersionList(wikiId);
  }
  
  @Override //위키 페이지 헤더에 프로젝트 정보 받기
  public ProjectCreateRequest getProjectInfo(String projectId){
    return wikiMapper.findProjectById(projectId);
  }
  
} // class end
