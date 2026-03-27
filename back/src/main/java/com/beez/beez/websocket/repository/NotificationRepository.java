package com.beez.beez.websocket.repository;

import jakarta.transaction.Transactional;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notifications, String> {
  @Modifying
  @Transactional
  @Query(value =
    "INSERT INTO notifications(id, user_id, content, created_on, status, link) " +
      "VALUES(generate_pk('notifications'), :userId, :content, SYSTIMESTAMP, 'G0', :link)",
    nativeQuery = true)
  void insertChat(
    @Param("userId") String userId,
    @Param("content") String content,
    @Param("link") String link
  );
  
  public List<Notifications> findAllByUserIdOrderByIdDesc(String userId);
}
