package com.beez.beez.file.web;

import com.beez.beez.file.dto.*;
import com.beez.beez.file.service.DocumentService;
import com.beez.beez.file.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class DocumentController {
  
  private final DocumentService documentService;
  
//  @PostMapping("/document/write/{projectId}")
//  public ResponseEntity<String> registerDocument(
//    @RequestPart("document") CreateRequest request,
//    @RequestPart(value = "files", required = false) List<MultipartFile> files) {
//
//    request.setFiles(files);
//    documentService.registerDocument(request);
//
//    return ResponseEntity.ok("문서 등록 성공");
//  }

  //수정해야할 수 있음 일단 보류
  @PostMapping(value = "/document/write" , consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<String> registerDocument(
    @ModelAttribute CreateRequest request, @RequestParam(value = "files", required = false) List<MultipartFile> files){
    return ResponseEntity.ok(documentService.registerDocument(request, files));
  }
  
  
  // 문서 수정
  @PutMapping("/document/update")
  public ResponseEntity<String> updateDocument(
    @RequestPart("document") UpdateRequest updateRequest,
    @RequestPart(value = "newFile", required = false) List<MultipartFile> newFiles
  ) {
    // 수정 시에도 프론트에서 넘겨준 작성자 정보를 사용하도록 서비스 호출
    documentService.updateDocument(updateRequest, newFiles, updateRequest.getUserId());
    
    return ResponseEntity.ok("문서 수정이 완료 되었습니다");
  }
  
  // 특정 프로젝트의 문서 목록 조회
  @GetMapping("/document/list/{projectId}")
  public ResponseEntity<List<ListResponse>> getDocumentList(
    @PathVariable String projectId) {
    List<ListResponse> list = documentService.getDocumentList(projectId);
    return ResponseEntity.ok(list);
  }
  
  // 문서 상세 조회
  @GetMapping("/document/detail/{id}")
  public ResponseEntity<DetailResponse> getDocumentDetail(
    @PathVariable String id) {
    DetailResponse detail = documentService.getDocumentDetail(id);
    if(detail == null) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(detail);
  }

  //파일 다운로드 기능
  @GetMapping("/document/download/{fileDetailId}")
  public ResponseEntity<Resource> downloadFile(@PathVariable String fileDetailId) {
    return documentService.downloadFile(fileDetailId);
  }

} //CLASS END