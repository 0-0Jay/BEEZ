package com.beez.beez.file.web;


import com.beez.beez.file.dto.DocumentRequest;
import com.beez.beez.file.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class DocumentController {
  
  private final DocumentService documentService;
  
  //새 게시글 작성
  @PostMapping("document/register")
  public ResponseEntity<String> registerDocument(
    @RequestPart("document")DocumentRequest.CreateRequest request,
    @RequestPart(value = "files", required = false)List<MultipartFile> files,
    @AuthenticationPrincipal User user) { //스프링 시큐리티 - 로그인한 유저의 정보를 직접 받음 - 이거 왜 필요한것? 나중에 권한 체크 하면 되는거 아닌가
    
    //사원의 id 추출 - 프론트에서 받으면 보안 위험있다고 해서 넣었음
    String userId = user.getUsername();
    
    request.setFiles(files);
    documentService.registerDocument(request, userId);
    
    return ResponseEntity.ok("문서 등록 성공");
  }
  
  //문서 수정
  @PutMapping("document/update")
  public ResponseEntity<String> updateDocument(
    @RequestPart("document") DocumentRequest.UpdateRequest updateRequest,
    @RequestPart(value = "newFile", required = false) List<MultipartFile> newFiles,
    @AuthenticationPrincipal User user) {
    
    String userId = user.getUsername();
    documentService.updateDocument(updateRequest, newFiles ,userId);
    
    return ResponseEntity.ok("문서 수정이 완료 되었습니다");
  }
  
  
}
