package com.beez.beez.project.mapper;

import com.beez.beez.project.dto.ProjectCreateRequest;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProjectMapper {
  String insertProject(ProjectCreateRequest dto);
}
