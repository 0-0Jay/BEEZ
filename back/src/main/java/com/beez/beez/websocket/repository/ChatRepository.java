package com.beez.beez.websocket.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRepository extends JpaRepository<Chat, String> {
  
  @Modifying
  @Transactional
  @Query(value =
    "INSERT INTO chat(id, project_id, user_id, content, created_on, parent_id) " +
      "VALUES(generate_pk('chat'), :projectId, :userId, :content, SYSTIMESTAMP, :parentId)",
    nativeQuery = true)
  void insertChat(
    @Param("projectId") String projectId,
    @Param("userId") String userId,
    @Param("content") String content,
    @Param("parentId") String parentId
  );
}
