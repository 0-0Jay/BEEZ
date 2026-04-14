package com.beez.beez.file.service;

import com.beez.beez.file.dto.FileDetailRequest;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileService {
  List<FileDetailRequest> saveFile(List<MultipartFile> files);
  
  //파일 다운로드 기능
  ResponseEntity<Resource> downloadFile(String fileDetailId);

}
