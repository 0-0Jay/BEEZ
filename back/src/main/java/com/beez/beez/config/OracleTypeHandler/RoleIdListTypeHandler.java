package com.beez.beez.config.OracleTypeHandler;

import oracle.jdbc.OracleConnection;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;
import org.apache.ibatis.type.TypeHandler;

import java.sql.*;
import java.util.List;

@MappedTypes(List.class)
public class RoleIdListTypeHandler implements TypeHandler<List<String>> {
  @Override
  public void setParameter(PreparedStatement ps, int i,
                           List<String> ids, JdbcType jdbcType) throws SQLException {
    OracleConnection conn = ps.getConnection().unwrap(OracleConnection.class);
    Object[] data = (ids == null || ids.isEmpty()) ? new Object[0] : ids.toArray();
    ps.setArray(i, conn.createOracleArray("ROLE_ID_TAB", data));
  }

  @Override public List<String> getResult(ResultSet rs, String col) { return null; }
  @Override public List<String> getResult(ResultSet rs, int idx) { return null; }
  @Override public List<String> getResult(CallableStatement cs, int idx) { return null; }
}