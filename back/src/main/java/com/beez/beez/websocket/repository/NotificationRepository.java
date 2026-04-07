package com.beez.beez.websocket.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notifications, String> {
  @Modifying
  @Transactional
  @Query(value =
    "INSERT INTO notifications(id, user_id, content, created_on, status, link, project_id) " +
      "VALUES(generate_pk('notifications'), :userId, :content, SYSTIMESTAMP, 'G1', :link, :projectId)",
    nativeQuery = true)
  void insertNotification(
    @Param("userId") String userId,
    @Param("content") String content,
    @Param("link") String link,
    @Param("projectId") String projectId
  );

}
