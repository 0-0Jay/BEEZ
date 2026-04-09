package com.beez.beez.file.service.impl;


import com.beez.beez.file.dto.DocumentRequest;
import com.beez.beez.file.dto.FileDetailRequest;
import com.beez.beez.file.mapper.DocumentMapper;
import com.beez.beez.file.service.DocumentService;
import com.beez.beez.file.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {
  
  private final DocumentMapper documentMapper;
  private final FileService fileService;
  
  @Override
  @Transactional //문서글(텍스트) + 파일묶음 = 1개의 게시글 형태로 만들기 위함
  public void registerDocument(DocumentRequest.CreateRequest request, String userId, String projectId){
    // 파일 바구니 생성
    String fileId = ""; //초기값 xml파일의 selectkey 가 채워줄거임
    documentMapper.insertFileMaster(fileId, userId);
    
    //파일 상세 정보 저장
    if(request.getFiles() != null && !request.getFiles().isEmpty()) {
      List<FileDetailRequest> saveFiles = fileService.saveFile(request.getFiles());
      
      for (FileDetailRequest file : saveFiles){
        file.setFileId(fileId); //파일 바구니 ID연결
        documentMapper.insertFileDetail(file);
      }
    }
    
    //문서 정보 저장
    documentMapper.insertDocument(request, userId, projectId);
  } // registerDocument end
  
  
  
@Override
@Transactional //update로직에 본인이 쓴 글만 수정할 수 있게 체크하는 로직을 추가해야할 수 있음
public void updateDocument(DocumentRequest.UpdateRequest updateRequest,
                           List<MultipartFile> newFiles,
                           String userId) {
    documentMapper.updateDocument(updateRequest);
    
    if(updateRequest.getFileUpdates() !=null) { //업데이트할때 파일있으면 처리하도록 함
      int fileIdx = 0;
      
      for(DocumentRequest.FileUpdateInfo updateInfo : updateRequest.getFileUpdates()) {
        
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
  public List<DocumentRequest.ListResponse> getDocumentList() {
    return documentMapper.selectDocumentList();
  }
  
  //상세 조회
  @Override
  public DocumentRequest.DetailResponse getDocumentDetail(String id){
    DocumentRequest.DetailResponse detail = documentMapper.selectDocumentDetail(id);
    detail.setFileList(documentMapper.selectLatestFilesByDocId(id));
    return detail;
  }
  
  
} //end
