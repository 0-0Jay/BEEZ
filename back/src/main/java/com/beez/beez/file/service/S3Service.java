package com.beez.beez.file.service;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

public interface S3Service {
  String uploadFile(MultipartFile file) throws IOException;
  byte[] downloadFile(String fileName);
  void deleteFile(String fileName);
  String getPresignedUrl(String fileName);
}