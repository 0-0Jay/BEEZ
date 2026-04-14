package com.beez.beez.file.service.impl;

import com.beez.beez.file.dto.FileDetailRequest;
import com.beez.beez.file.mapper.DocumentMapper;
import com.beez.beez.file.service.FileService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {
  // 저장소 위치 불러오기
  @Value("${file.upload.path}")
  private String uploadPath;
  private final DocumentMapper documentMapper;
  
  
  @Override
  public List<FileDetailRequest> saveFile(List<MultipartFile> files) {
    List<FileDetailRequest> fileDetails = new ArrayList<>();
    
    if (files != null && !files.isEmpty()) {
      for (MultipartFile file : files) {
        String originalName = file.getOriginalFilename();
        String extension = originalName != null && originalName.contains(".")
          ? originalName.substring(originalName.lastIndexOf(".") + 1) : "";
        String storedName = UUID.randomUUID().toString() + (extension.isEmpty() ? "" : "." + extension);
        
        // 실제 파일 저장 부분 + 추후에 S3 전용 코드로 바꿔야 함!
        try {
          Path savePath = Paths.get(uploadPath, storedName);
          Files.createDirectories(savePath.getParent());
          file.transferTo(savePath.toFile());
        } catch (IOException e) {
          throw new RuntimeException("파일 저장 실패: " + originalName, e);
        }
        
        fileDetails.add(FileDetailRequest.builder()
          .originalName(originalName)
          .storedName(storedName)
          .extension(extension)
          .fileSize(file.getSize())
          .build());
        
      }
    }
    
    return fileDetails;
  }
  //파일 다운로드
  @Override
  public ResponseEntity<Resource> downloadFile(String fileDetailId){
    //DB에서 파일 정보 조회
    FileDetailRequest file = documentMapper.selectFileDetailById(fileDetailId);
    
    //실제 파일 경로
    Path filePath = Paths.get(uploadPath, file.getStoredName());
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
  
}
