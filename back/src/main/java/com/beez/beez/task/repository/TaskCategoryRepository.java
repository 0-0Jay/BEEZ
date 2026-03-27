package com.beez.beez.task.repository;

import jakarta.transaction.Transactional;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskCategoryRepository extends JpaRepository<TaskCategory, String> {
  // 일감 범주 생성
  @Modifying
  @Transactional
  @Query(value =
    "INSERT INTO task_category(id, name, description) " +
      "VALUES(generate_pk('task_category'), :name)",
    nativeQuery = true)
  void insertTaskCategory(@Param("name") String name, @Param("description") String description);
}
