package com.beez.beez.file.service.impl;

import com.beez.beez.file.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.IOException;
import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3ServiceImpl implements S3Service {
  
  private final S3Client s3Client;
  private final S3Presigner s3Presigner;
  
  @Value("${cloud.aws.s3.bucket}")
  private String bucket;
  
  @Value("${cloud.aws.region.static}")
  private String region;
  
  // 업로드
  @Override
  public String uploadFile(MultipartFile file, String storedName) throws IOException {
    
    PutObjectRequest request = PutObjectRequest.builder()
      .bucket(bucket)
      .key(storedName)
      .contentType(file.getContentType())
      .contentLength(file.getSize())
      .build();
    
    s3Client.putObject(request, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
    
    return "https://" + bucket + ".s3." + region + ".amazonaws.com/" + storedName;
  }
  
  // 다운로드
  @Override
  public byte[] downloadFile(String fileName) {
    GetObjectRequest request = GetObjectRequest.builder()
      .bucket(bucket)
      .key(fileName)
      .build();
    
    return s3Client.getObjectAsBytes(request).asByteArray();
  }
  
  // 삭제
  @Override
  public void deleteFile(String fileName) {
    DeleteObjectRequest request = DeleteObjectRequest.builder()
      .bucket(bucket)
      .key(fileName)
      .build();
    
    s3Client.deleteObject(request);
  }
  
  @Override
  public String getPresignedUrl(String fileName) {
    GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
      .signatureDuration(Duration.ofMinutes(15))
      .getObjectRequest(r -> r.bucket(bucket).key(fileName))
      .build();
    
    return s3Presigner.presignGetObject(presignRequest).url().toString();
  }
}