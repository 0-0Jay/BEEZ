package com.beez.beez.notification.repository;

import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notifications, String> {
  // 특정 유저의 모든 알림 조회 (최신순)
  List<Notifications> findByUserIdOrderByCreatedOnDesc(String userId);
  
  // 특정 유저의 읽지 않은 알림 수 조회
  @Query("SELECT COUNT(n) FROM Notifications n")
  long countByUserIdAndReadFalse(String userId);
  
  // 특정 유저의 모든 알림 삭제
  @Modifying
  @Query("DELETE FROM Notifications n WHERE n.userId = :userId")
  void deleteAllByUserId(@Param("userId") String userId);
  
  // 특정 유저의 읽지 않은 알림 전체 읽음 처리
  @Modifying
  @Query("UPDATE Notifications n SET n.status = '1' WHERE n.userId = :userId AND n.status = '0'")
  void markAllAsReadByUserId(@Param("userId") String userId);
}
