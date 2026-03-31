package com.beez.beez.task.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, String> {
  @Modifying
  @Transactional
  @Query(value =
    "INSERT INTO files(id, created_on, user_id, is_delete) " +
      "VALUES(generate_pk('files'), SYSTIMESTAMP, :userId, 'F0')",
    nativeQuery = true)
  void insertFiles(
    @Param("userId") String userId
  );
  
}
