package com.beez.beez.file.service;

import com.beez.beez.file.dto.FileDetailRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileService {
  List<FileDetailRequest> saveFile(List<MultipartFile> files);
}
