package com.beez.beez.file.service.impl;

import com.beez.beez.file.dto.FileDetailRequest;
import com.beez.beez.file.service.FileService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {
  // 저장소 위치 불러오기
  @Value("${file.upload.path}")
  private String uploadPath;
  
  @Override
  public List<FileDetailRequest> saveFile(List<MultipartFile> files) {
    List<FileDetailRequest> fileDetails = new ArrayList<>();
    
    if (files != null && !files.isEmpty()) {
      for (MultipartFile file : files) {
        String originalName = file.getOriginalFilename();
        String extension = originalName != null && originalName.contains(".")
          ? originalName.substring(originalName.lastIndexOf(".") + 1) : "";
        String storedName = UUID.randomUUID().toString() + (extension.isEmpty() ? "" : "." + extension);
        
        // 실제 파일 저장 부분
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
}
