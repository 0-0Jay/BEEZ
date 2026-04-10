package com.beez.beez.config.OracleTypeHandler;

import com.beez.beez.group.dto.GroupProjectDto;
import oracle.jdbc.OracleConnection;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;
import org.apache.ibatis.type.TypeHandler;

import java.sql.*;
import java.util.List;

@MappedTypes(List.class)
public class ProjectArrayTypeHandler implements TypeHandler<List<GroupProjectDto>> {

  @Override
  public void setParameter(PreparedStatement ps, int i,
                           List<GroupProjectDto> projects, JdbcType jdbcType) throws SQLException {
    OracleConnection conn = ps.getConnection().unwrap(OracleConnection.class);

    Struct[] structs = new Struct[projects.size()];
    for(int j = 0; j < projects.size(); j++) {
      GroupProjectDto p = projects.get(j);
      Object[] objAttributes = new Object[] { p.getProjectId(), p.getRoleId() };
      structs[j] = conn.createStruct("PROJECT_OBJ", objAttributes);
    }

    ps.setArray(i, conn.createOracleArray("PROJECT_TAB", structs));
  }

  @Override public List<GroupProjectDto> getResult(ResultSet rs, String col) { return null; }
  @Override public List<GroupProjectDto> getResult(ResultSet rs, int idx) { return null; }
  @Override public List<GroupProjectDto> getResult(CallableStatement cs, int idx) { return null; }
}
