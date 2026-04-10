package com.beez.beez.config.OracleTypeHandler;

import com.beez.beez.group.dto.GroupProjectDto;
import oracle.jdbc.OracleConnection;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;
import org.apache.ibatis.type.TypeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@MappedTypes(List.class)
public class ArrayTypeHandler implements TypeHandler<List<String>> {
  @Override
  public void setParameter(PreparedStatement ps, int i,
                           List<String> ids, JdbcType jdbcType) throws SQLException {
    OracleConnection conn = ps.getConnection().unwrap(OracleConnection.class);
    Object[] data = (ids == null || ids.isEmpty()) ? new Object[0] : ids.toArray();
    ps.setArray(i, conn.createOracleArray("USER_ID_TAB", data));
  }

  @Override public List<String> getResult(ResultSet rs, String col) { return null; }
  @Override public List<String> getResult(ResultSet rs, int idx) { return null; }
  @Override public List<String> getResult(CallableStatement cs, int idx) { return null; }
}
