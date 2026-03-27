package com.beez.beez.task.repository;

import jakarta.transaction.Transactional;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskTypeRepository extends JpaRepository<TaskType, String> {
  // 일감 유형 생성
  @Modifying
  @Transactional
  @Query(value =
    "INSERT INTO task_type(id, name, default_status, description) " +
      "VALUES(generate_pk('task_type'), :name, :status, :description)",
    nativeQuery = true)
  void insertTaskType(@Param("name") String name, @Param("status") String defaultStatus, @Param("description") String description);
}
