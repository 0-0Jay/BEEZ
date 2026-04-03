package com.beez.beez.config.OracleTypeHandler;

import com.beez.beez.task.dto.JournalRequest;
import oracle.jdbc.driver.OracleConnection;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;
import org.apache.ibatis.type.TypeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@MappedTypes(List.class)
public class JournalTypeHandler implements TypeHandler<List<JournalRequest>> {
  
  @Override
  public void setParameter(PreparedStatement ps, int i,
                                  List<JournalRequest> journals, JdbcType jdbcType) throws SQLException {
    OracleConnection conn = ps.getConnection().unwrap(OracleConnection.class);
    
    Object[] structs = new Object[journals.size()];
    for (int j = 0; j < journals.size(); j++) {
      JournalRequest r = journals.get(j);
      Object[] attrs = { r.getFieldName(), r.getOldValue(), r.getNewValue() };
      structs[j] = conn.createStruct("T_JOURNAL", attrs);
    }
    
    ps.setArray(i, conn.createOracleArray("T_JOURNAL_LIST", structs));
  }
  
  @Override public List<JournalRequest> getResult(ResultSet rs, String col) { return null; }
  @Override public List<JournalRequest> getResult(ResultSet rs, int idx) { return null; }
  @Override public List<JournalRequest> getResult(CallableStatement cs, int idx) { return null; }
}