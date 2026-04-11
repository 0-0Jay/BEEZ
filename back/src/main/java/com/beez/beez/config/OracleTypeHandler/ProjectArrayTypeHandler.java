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

    if (projects == null) {
      ps.setNull(i, Types.ARRAY, "PROJECT_TAB");
      return;
    }

    Struct[] structs = new Struct[projects.size()];
    for (int j = 0; j < projects.size(); j++) {
      GroupProjectDto p = projects.get(j);

      Array roleIdsArray = conn.createOracleArray("ROLE_ID_TAB", p.getRoleIds().toArray());

      Object[] objAttributes = new Object[] {
        p.getProjectId(),
        roleIdsArray
      };

      structs[j] = conn.createStruct("PROJECT_REC", objAttributes);
    }
    Array projectArray = conn.createOracleArray("PROJECT_TAB", structs);
    ps.setArray(i, projectArray);
  }

  @Override public List<GroupProjectDto> getResult(ResultSet rs, String col) { return null; }
  @Override public List<GroupProjectDto> getResult(ResultSet rs, int idx) { return null; }
  @Override public List<GroupProjectDto> getResult(CallableStatement cs, int idx) { return null; }
}
