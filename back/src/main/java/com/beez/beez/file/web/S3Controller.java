package com.beez.beez.file.web;

import com.beez.beez.file.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.UUID;
import java.io.IOException;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class S3Controller {
  
  private final S3Service s3Service;
  
  // 업로드
  @PostMapping("/upload")
  public ResponseEntity<String> upload(@RequestParam("file") MultipartFile file) throws IOException {
    
    String originalName = file.getOriginalFilename();
    String extension = "";
    
    if (originalName != null && originalName.contains(".")) {
      extension = originalName.substring(originalName.lastIndexOf("."));
    }
    
    String storedName = UUID.randomUUID().toString() + extension;
    
    String url = s3Service.uploadFile(file, storedName);
    
    return ResponseEntity.ok(url);
  }
  
  // 다운로드
  @GetMapping("/download/{fileName}")
  public ResponseEntity<byte[]> download(@PathVariable String fileName) {
    byte[] data = s3Service.downloadFile(fileName);
    return ResponseEntity.ok()
      .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
      .body(data);
  }
  
  // 삭제
  @DeleteMapping("/{fileName}")
  public ResponseEntity<Void> delete(@PathVariable String fileName) {
    s3Service.deleteFile(fileName);
    return ResponseEntity.noContent().build();
  }
  
  // Presigned URL 발급
  @GetMapping("/presigned/{fileName}")
  public ResponseEntity<String> getPresignedUrl(@PathVariable String fileName) {
    String url = s3Service.getPresignedUrl(fileName);
    return ResponseEntity.ok(url);
  }
}