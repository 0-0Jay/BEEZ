package com.beez.beez.file.service.impl;


import com.beez.beez.file.dto.*;
import com.beez.beez.file.mapper.DocumentMapper;
import com.beez.beez.file.service.DocumentService;
import com.beez.beez.file.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.jpa.domain.JpaSort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {
  
  private final DocumentMapper documentMapper;
  private final FileService fileService;
  
  @Override
  @Transactional //문서글(텍스트) + 파일묶음 = 1개의 게시글 형태로 만들기 위함
  public String registerDocument(CreateRequest request, List<MultipartFile> files){

    request.setFiles(files);
    System.out.println("projectId: " + request.getProjectId());
    System.out.println("userId: " + request.getUserId());
    System.out.println("title: " + request.getTitle());


    // 파일 바구니 생성
    documentMapper.insertFileMaster(request);
    request.setFileId(request.getId());
    
    //파일 상세 정보 저장
    if(request.getFiles() != null && !request.getFiles().isEmpty()) {
      List<FileDetailRequest> saveFiles = fileService.saveFile(request.getFiles());
      
      for (FileDetailRequest file : saveFiles){
        file.setFileId(request.getFileId()); //파일 바구니 ID연결
        documentMapper.insertFileDetail(file);
      }
    }
    
    //문서 정보 저장
    documentMapper.insertDocument(request);
    return"";
  } // registerDocument end
  
  
  
@Override
@Transactional //update로직에 본인이 쓴 글만 수정할 수 있게 체크하는 로직을 추가해야할 수 있음
public void updateDocument(UpdateRequest updateRequest,
                           List<MultipartFile> newFiles,
                           String userId) {
    documentMapper.updateDocument(updateRequest);
    
    if(updateRequest.getFileUpdates() !=null) { //업데이트할때 파일있으면 처리하도록 함
      int fileIdx = 0;
      
      for(FileUpdateInfo updateInfo : updateRequest.getFileUpdates()) {
        
        if(updateInfo.isDeleted()){ //삭제 버튼 클릭시
          documentMapper.deleteFileDetail(updateInfo.getTargetFileDetailId()); //소프트 딜리트 처리
        } else if (newFiles != null && fileIdx < newFiles.size()) { //새파일이 교체되면 아래 실행
          //기존 파일 정보 조회(root_id와 현재 version 확인용)
          //파일의 root_id를 찾아옴
          FileDetailRequest oldFile = documentMapper.selectFileDetailById(updateInfo.getTargetFileDetailId());
          
          //새파일 저장
          MultipartFile currentFile = newFiles.get(fileIdx++);
          List<MultipartFile> singleFileList = List.of(currentFile);
          FileDetailRequest newFile = fileService.saveFile(singleFileList).get(0);
          
          //버전 정보 세팅
          newFile.setFileId(oldFile.getFileId()); // 바구니 번호 연결
          newFile.setVersion(oldFile.getVersion() + 1); // 버전 v1 -> v2
          newFile.setRootId(oldFile.getRootId()); // root_id번호 연결
          
          //DB에 새버전을 삽입
          documentMapper.insertFileDetail(newFile);
          
        } //else if end
      } //for end
    } // if end
  }
  
  @Override //목록 조회
  public List<ListResponse> getDocumentList(String projectId) {
    return documentMapper.selectDocumentList();
  }
  
  //상세 조회
  @Override
  public DetailResponse getDocumentDetail(String id){
    DetailResponse detail = documentMapper.selectDocumentDetail(id);
    List<FileDetailResponse> files = documentMapper.selectLatestFilesByDocId(id);
    System.out.println("docId: " + id);
    System.out.println("fileList size: " + files.size());
    System.out.println("fileList: " + files);
    detail.setFileList(files);
    return detail;
  }

  //파일 다운로드
  @Value("D:/beezfile")
  private String uploadPath;

  @Override
  public ResponseEntity<Resource> downloadFile(String fileDetailId){
    //DB에서 파일 정보 조회
    FileDetailRequest file = documentMapper.selectFileDetailById(fileDetailId);

    System.out.println("storedName: " + file.getStoredName());  // ← 추가
    System.out.println("uploadPath: " + uploadPath);            // ← 추가

    //실제 파일 경로
      Path filePath = Paths.get(uploadPath, file.getStoredName());
    System.out.println("fullPath: " + filePath.toAbsolutePath()); // ← 추가
      Resource resource = new FileSystemResource(filePath);

    if(!resource.exists()){
      throw new RuntimeException("파일을 찾을 수 없습니다.");
    }

    //한글 파일명 깨짐 방지
    String encodedName = UriUtils.encode(file.getOriginalName(), StandardCharsets.UTF_8);

    return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedName + "\"")
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .body(resource);
  }
  
} //end
