package com.beez.beez.config.OracleTypeHandler;

import oracle.jdbc.OracleTypes;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.*;

@MappedJdbcTypes(JdbcType.CURSOR)
@MappedTypes(java.sql.ResultSet.class)
public class CursorTypeHandler extends BaseTypeHandler<ResultSet> {
  
  @Override
  public void setNonNullParameter(PreparedStatement ps, int i, ResultSet rs, JdbcType jdbcType) throws SQLException {
    // OUT 파라미터는 setParameter 불필요
  }
  
  @Override
  public ResultSet getNullableResult(ResultSet rs, String columnName) throws SQLException {
    return (ResultSet) rs.getObject(columnName);
  }
  
  @Override
  public ResultSet getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
    return (ResultSet) rs.getObject(columnIndex);
  }
  
  @Override
  public ResultSet getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
    return (ResultSet) cs.getObject(columnIndex);
  }
}