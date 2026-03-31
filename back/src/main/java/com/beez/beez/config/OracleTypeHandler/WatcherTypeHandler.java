package com.beez.beez.config.OracleTypeHandler;

import oracle.jdbc.OracleConnection;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import java.sql.*;
import java.util.List;

@MappedTypes(String[].class)
public class WatcherTypeHandler extends BaseTypeHandler<List<String>> {
  @Override
  public void setNonNullParameter(PreparedStatement ps, int i, List<String> list, JdbcType jdbcType) throws SQLException {
    Connection conn = ps.getConnection();
    oracle.jdbc.OracleConnection oracleConn = conn.unwrap(OracleConnection.class);
    Array array = oracleConn.createOracleArray("WATCHER_LIST", list.toArray());
    ps.setArray(i, array);
  }
  
  @Override
  public List<String> getNullableResult(ResultSet rs, String columnName) throws SQLException {
    return null;
  }
  
  @Override
  public List<String> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
    return null;
  }
  
  @Override
  public List<String> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
    return null;
  }
}
