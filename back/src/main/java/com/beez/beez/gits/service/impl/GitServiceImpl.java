package com.beez.beez.gits.service.impl;

import com.beez.beez.gits.dto.GitCommitRequest;
import com.beez.beez.gits.dto.GitRepoDto;
import com.beez.beez.gits.mapper.GitMapper;
import com.beez.beez.gits.service.GitService;
import lombok.RequiredArgsConstructor;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.revwalk.RevCommit;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.apache.tomcat.util.http.fileupload.FileUtils.deleteDirectory;

@Service
@RequiredArgsConstructor
@Transactional
public class GitServiceImpl implements GitService {

  private final GitMapper gitMapper;

  // 상대경로 설정
  private final String REPO_ROOT = "./git_repos";

  // 저장소 등록 및 복제
  @Override
  public void insertRepository(GitRepoDto dto) throws Exception {
    // 중복 체크
    int count = gitMapper.checkRepoExists(dto);
    if(count > 0) {
      throw new RuntimeException("이미 동일한 이름이나 URL이 등록된 저장소가 있습니다.");
    }

    // PK 생성 함수 호출
    String newRepoId = gitMapper.getNextPk("repositories");

    // 상대 경로로 폴더 객체 생성
    File repoDir = new File(REPO_ROOT, newRepoId);

    // 폴더 없으면 미리 생성
    if (!repoDir.exists()) {
      boolean created = repoDir.mkdirs();
      if (!created) {
        // 폴더 생성 실패 시 예외를 던져서 이후 Clone이 진행되지 않게 막음
        throw new IOException("저장소 폴더를 생성할 수 없습니다: " + repoDir.getAbsolutePath());
      }
    }

    dto.setId(newRepoId);
    dto.setLocalPath(repoDir.getPath());

    // repoType이 없으면 일반저장소(AA0)로 기본값 설정
    if (dto.getRepoType() == null || dto.getRepoType().isEmpty()) {
      dto.setRepoType("AA0");
    }

    // JGit Clon 실행
    Git.cloneRepository()
      .setURI(dto.getRepoUrl())
      .setDirectory(repoDir)
      .setBare(true)
      .call();

    // 등록
    gitMapper.insertRepository(dto);

  }

  // 커밋 동기화
  @Override
  public int updateSyncCommits(String id) throws Exception {
    GitRepoDto repo = gitMapper.findReposById(id);

    if(repo == null || repo.getLocalPath() == null) {
      throw new RuntimeException("저장소 정보를 찾을 수 없거나 경로가 설정되지 않았습니다.");
    }
    String localPath = repo.getLocalPath();
    int count = 0;

    File localDir = new File(repo.getLocalPath());

    if (!localDir.exists()) {
      if (!localDir.mkdirs()) {
        if (!localDir.exists()) {
          throw new IOException("저장소 경로를 생성할 수 없습니다: " + localPath);
        }
      }

      // 재복제 실행
      Git.cloneRepository()
        .setURI(repo.getRepoUrl())
        .setDirectory(localDir)
        .setBare(true)
        .call();
    }

    // 저장소 열기
    try(Git git = Git.open(new File(localPath))){
      // 원격 저장소에서 최신 데이터(커밋 이력)를 가져옴
      try {
        // 원격 저장소에서 데이터 가져오기 (여기서 네트워크 에러가 자주 남)
        git.fetch().setRemote("origin").call();
      } catch (Exception e) {
        throw new RuntimeException("원격 저장소와 통신에 실패했습니다");
      }
      Iterable<RevCommit> commits = git.log().all().call();
//      Iterable<RevCommit> commits = git.log()
//        .add(git.getRepository().resolve("refs/remotes/origin/main") != null
//          ? git.getRepository().resolve("refs/remotes/origin/main")
//          : git.getRepository().resolve("refs/remotes/origin/master"))
//        .call();
      Pattern p = Pattern.compile("#(TASK\\d+)", Pattern.CASE_INSENSITIVE); // #숫자 꺼냄

      for(RevCommit commit : commits){
// System.out.println("Commit Message: " + commit.getFullMessage());
        String msg = commit.getFullMessage();
        Matcher m = p.matcher(msg);
        boolean found = false;

        GitCommitRequest request = new GitCommitRequest();
        request.setCommitSha(commit.getName());
        request.setRepoId(id);
        request.setMessage(msg);
        request.setAuthor(commit.getAuthorIdent().getName());
        request.setCommitDate(new Timestamp(commit.getCommitTime() * 1000L));

        while(m.find()){
          found = true;
          request.setTaskId(m.group(1).toUpperCase());
          // 쿼리 실행
          count += gitMapper.updateCommitLog(request);
        }
        if(!found){
          request.setTaskId(null);
          count += gitMapper.updateCommitLog(request);
        }
      }
    }catch (IOException e) {
      // 파일 경로나 권한 문제
      throw new RuntimeException("로컬 저장소 파일을 열 수 없습니다.");
    } catch (Exception e) {
      // 그 외 예상치 못한 에러
      throw new RuntimeException("동기화 중 오류가 발생했습니다.");
    }
    return count;
  }

  // 일감 번호로 커밋 목록 조회
  @Override
  public List<GitCommitRequest> findCommitsByTaskId(String taskId) {
    return gitMapper.findCommitsByTaskId(taskId);
  }

  // 프로젝트별 저장소 조회
  @Override
  public List<GitRepoDto> findReposByProjectId(String projectId) {
    return gitMapper.findReposByProjectId(projectId);
  }

  // 저장소 삭제(해제) - DB 데이터 삭제
  @Override
  public void deleteRepository(String id) {
    GitRepoDto repo = gitMapper.findReposById(id);

    if (repo == null) {
      throw new RuntimeException("삭제할 저장소 정보가 존재하지 않습니다.");
    }

    // DB 데이터 삭제
    gitMapper.deleteCommitLogsByRepoId(id);
    gitMapper.deleteRepository(id);

    // 물리적 폴더 삭제
    File repoDir = new File(repo.getLocalPath());
    if (repoDir.exists()) {
      deleteDirectory(repoDir);
    }
  }

  // 저장소 삭제(해제) - 물리적 폴더 삭제
  private void deleteDirectory(File directory) {
    File[] allContents = directory.listFiles();
    if (allContents != null) {
      for (File file : allContents) {
        // 하위 파일/폴더 삭제 시도
        deleteDirectory(file);
      }
    }

    try {
      if (directory.exists() && !directory.delete()) {
        System.err.println("삭제 실패 (권한/잠김): " + directory.getAbsolutePath());
      }
    } catch (SecurityException e) {
      System.err.println("권한 거부: " + e.getMessage());
    }
  }


}
