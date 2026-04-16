package com.beez.beez.gits.service.impl;

import com.beez.beez.gits.dto.GitCommitRequest;
import com.beez.beez.gits.dto.GitRepoDto;
import com.beez.beez.gits.mapper.GitMapper;
import com.beez.beez.gits.service.GitService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.revwalk.RevCommit;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class GitServiceImpl implements GitService {

  private final GitMapper gitMapper;

  // 상대경로 설정
  private final String REPO_ROOT = "./git_repos";

  // 저장소 등록 및 복제
  @Override
  public void insertRepository(GitRepoDto dto) throws Exception {
    // 중복 체크
    int count = gitMapper.checkRepoExists(dto);

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
  @Transactional
  @Override
  public int updateSyncCommits(String id) throws Exception {

    GitRepoDto repo = gitMapper.findReposById(id);

    if (repo == null || repo.getLocalPath() == null) {
      throw new RuntimeException("저장소 정보를 찾을 수 없거나 경로가 설정되지 않았습니다.");
    }

    String localPath = repo.getLocalPath();
    int count = 0;

    File localDir = new File(localPath);

    // 로컬 저장소 없으면 재복제
    if (!localDir.exists()) {
      if (!localDir.mkdirs() && !localDir.exists()) {
        throw new IOException("저장소 경로를 생성할 수 없습니다: " + localPath);
      }

      Git.cloneRepository()
        .setURI(repo.getRepoUrl())
        .setDirectory(localDir)
        .setBare(true)
        .call();
    }

    Git git = null;

    try {
      git = Git.open(localDir);

      // fetch
      git.fetch().setRemote("origin").call();

      Iterable<RevCommit> commits = git.log().all().call();

      Pattern pattern = Pattern.compile("#(TASK\\d+)", Pattern.CASE_INSENSITIVE);

      for (RevCommit commit : commits) {

        String message = commit.getFullMessage();
        Matcher matcher = pattern.matcher(message);

        GitCommitRequest request = new GitCommitRequest();
        request.setCommitSha(commit.getName());
        request.setRepoId(id);
        request.setMessage(message);
        request.setAuthor(commit.getAuthorIdent().getName());
        request.setCommitDate(new Timestamp(commit.getCommitTime() * 1000L));

        boolean hasTask = false;

        while (matcher.find()) {
          hasTask = true;

          String taskId = matcher.group(1).toUpperCase();

          if (!taskExists(taskId)) {
            request.setTaskId(null);
          } else {
            request.setTaskId(taskId);
          }

          count += gitMapper.updateCommitLog(request);
        }

        if (!hasTask) {
          request.setTaskId(null);
          count += gitMapper.updateCommitLog(request);
        }
      }

    } finally {
      if (git != null) {
        git.getRepository().close();
        git.close();
      }
    }

//    System.out.println("최종 저장된 개수: " + count);
    return count;
  }

  private boolean taskExists(String taskId) {
    return gitMapper.existsTask(taskId) > 0;
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

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void deleteRepo(String id) {
    gitMapper.deleteRepository(id);
  }

  // 저장소 삭제(해제) - DB 데이터 삭제
  @Override
  public void deleteRepository(String id) {
    try {
      System.out.println("1. repo 조회");

      GitRepoDto repo = gitMapper.findReposById(id);

      if (repo == null) {
        throw new RuntimeException("삭제할 저장소 정보가 존재하지 않습니다.");
      }

      System.out.println("2. commit 삭제");
      try {
        int deleted = gitMapper.deleteCommitLogsByRepoId(id);
        System.out.println("삭제된 commit 개수: " + deleted);

      } catch (Exception e) {
        e.printStackTrace();
        throw e;
      }

      System.out.println("3. repo 삭제");
      deleteRepo(id);

      System.gc();
      try {
        Thread.sleep(200);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }

      System.out.println("4. 폴더 삭제");
      File repoDir = new File(repo.getLocalPath());
      if (repoDir.exists()) {
        FileUtils.deleteDirectory(repoDir);
      }

      System.out.println("5. 완료");

    } catch (IOException e) {
      e.printStackTrace();
//      throw e;
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
