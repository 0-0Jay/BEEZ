package com.beez.beez.wiki.service.impl;

import com.beez.beez.wiki.dto.WikiRequest;
import com.beez.beez.wiki.dto.WikiVersionRequest;
import com.beez.beez.wiki.mapper.WikiMapper;
import com.beez.beez.wiki.service.WikiService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

// 로직 구현체 파일 - 실제로 어떻게 동작하는지 코드 작성
@Service
public class WikiServiceImpl implements WikiService {
  
  //
  @Autowired
  private WikiMapper wikiMapper;
  
  @Override
  @Transactional
  public void createWiki(WikiRequest wikiRequest, WikiVersionRequest wikiVersionRequest){
    //위키 기본 정보 저장
    wikiMapper.insertWiki(wikiRequest);
    
    //새로 저장된 위키의 ID를 버전 정보의 wikiId 필드에 세팅
    wikiVersionRequest.setWikiId(wikiRequest.getId());
    
    //위키 본문 내용 저장
    wikiMapper.insertWikiVersion(wikiVersionRequest);
  }
  
  //프로젝트 ID넘겨서 wiki list 받아오기
  @Override
  public List<WikiRequest> getWikiList(String projectId){
    return wikiMapper.selectWikiList(projectId);
  }
  
  //위키에게ID주고 최신 버전 받아오기
  @Override
  public WikiVersionRequest getWikiDetail(String wikiId){
    return wikiMapper.selectLatestVersion(wikiId);
  }
  
}
