package com.beez.beez.notification.repository;

import jakarta.transaction.Transactional;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, String> {
  // 특정 유저의 모든 알림 조회 (최신순)
  List<Notification> findByUserIdOrderByCreatedAtDesc(String userId);
  
  // 특정 유저의 읽지 않은 알림 수 조회
  long countByUserIdAndReadFalse(String userId);
  
  // 특정 유저의 모든 알림 삭제
  @Modifying
  @Query("DELETE FROM Notification n WHERE n.userId = :userId")
  void deleteAllByUserId(@Param("userId") String userId);
  
  // 특정 유저의 읽지 않은 알림 전체 읽음 처리
  @Modifying
  @Query("UPDATE Notification n SET n.read = true WHERE n.userId = :userId AND n.read = false")
  void markAllAsReadByUserId(@Param("userId") String userId);
}
