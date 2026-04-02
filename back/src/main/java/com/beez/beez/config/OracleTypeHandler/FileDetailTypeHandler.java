package com.beez.beez.config.OracleTypeHandler;

import com.beez.beez.file.dto.FileDetailRequest;
import oracle.jdbc.OracleConnection;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;
import java.sql.*;
import java.util.List;

@MappedTypes(List.class)
public class FileDetailTypeHandler extends BaseTypeHandler<List<FileDetailRequest>> {
  
  @Override
  public void setNonNullParameter(PreparedStatement ps, int i,
                                  List<FileDetailRequest> files, JdbcType jdbcType) throws SQLException {
    
    OracleConnection oracleConn = ps.getConnection().unwrap(OracleConnection.class);
    
    Object[] structs = new Object[files.size()];
    for (int j = 0; j < files.size(); j++) {
      FileDetailRequest f = files.get(j);
      Object[] attrs = { f.getOriginalName(), f.getStoredName(), f.getExtension(), f.getFileSize() };
      structs[j] = oracleConn.createStruct("T_FILE_DETAIL", attrs);
    }
    
    Array array = oracleConn.createOracleArray("T_FILE_DETAIL_LIST", structs);
    ps.setArray(i, array);
  }
  
  @Override public List<FileDetailRequest> getNullableResult(ResultSet rs, String col) { return null; }
  @Override public List<FileDetailRequest> getNullableResult(ResultSet rs, int idx) { return null; }
  @Override public List<FileDetailRequest> getNullableResult(CallableStatement cs, int idx) { return null; }
}
