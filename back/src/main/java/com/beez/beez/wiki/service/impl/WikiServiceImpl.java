package com.beez.beez.wiki.service.impl;

import com.beez.beez.wiki.dto.WikiProjectRequest;
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
  @Transactional // 프로시저 처리 하면 성능 향상 가능 - TaskMapper 파일 제일 밑에 형태 참고
  public void insertWiki(WikiRequest wikiRequest, WikiVersionRequest versionRequest){
    wikiMapper.insertWikiVersion(versionRequest);
    wikiRequest.setVersionId(versionRequest.getVersionId());
    
    wikiMapper.insertWiki(wikiRequest);
    System.out.println("생성된 위키 ID: " + wikiRequest.getId()); // 이게 null인지 확인!
    //builder처리 하면 set작업 여러번 할필요 없음 -TaskServiceImpl 파일에 예시 있음
    versionRequest.setWikiId(wikiRequest.getId());
    System.out.println("셋팅된 버전용 위키 ID: " + versionRequest.getWikiId()); // 이것도 확인!
    wikiMapper.updateWikiIdInVersion(versionRequest);
  }
  
  @Override
  @Transactional // 수정시 본문 추가랑 wiki테이블 최신 버전 갱신
  public void updateWikiContent(WikiVersionRequest wikiVersionRequest, WikiRequest wikiRequest){
    wikiMapper.insertWikiVersion(wikiVersionRequest);
    // 2. 새로 생성된 PK를 위키 마스터 테이블용 객체에 전달
    wikiRequest.setVersionId(wikiVersionRequest.getVersionId());
    wikiMapper.updateWiki(wikiRequest);
  }
  
  @Override //projectId별로 위키목록 조회
  public List<WikiRequest> findWikiByProjectId(String projectId){
    return wikiMapper.findWikiByProjectId(projectId);
  }
  
  @Override
  public WikiVersionRequest findLatestVersion(String wikiId){
    WikiVersionRequest result = wikiMapper.findLatestVersion(wikiId);
    System.out.println("★ DB에서 가져온 Links: " + (result != null ? result.getLinks() : "결과없음"));
    return result;
  }
  
  @Override //wikiId별로 히스토리 조회
  public List<WikiVersionRequest> findWikiVersionList(String wikiId){
    return wikiMapper.findWikiVersionList(wikiId);
  }
  
  @Override //위키 페이지 헤더에 프로젝트 정보 받기
  public WikiProjectRequest getProjectInfo(String projectId){
    return wikiMapper.findProjectById(projectId);
  }
  
  @Override
  public WikiVersionRequest findLatestByProjectId(String projectId) {
    List<WikiRequest> wikiList = wikiMapper.findWikiByProjectId(projectId);
    if (wikiList == null || wikiList.isEmpty()) {
      return null;
    }
    String latestWikiId = wikiList.get(0).getId();
    return findLatestVersion(latestWikiId);
  }
  
} // class end
