package com.beez.beez.config.OracleTypeHandler;

import com.beez.beez.workflow.dto.WorkflowItemRequest;
import oracle.jdbc.OracleConnection;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import java.sql.*;
import java.util.List;

@MappedTypes(List.class)
public class WorkflowTypeHandler extends BaseTypeHandler<List<WorkflowItemRequest>> {

  @Override
  public void setNonNullParameter(PreparedStatement ps, int i, List<WorkflowItemRequest> parameter, JdbcType jdbcType) throws SQLException {
    if (parameter == null) return;

    OracleConnection conn = ps.getConnection().unwrap(OracleConnection.class);
    Struct[] structs = new Struct[parameter.size()];

    for(int idx=0; idx <parameter.size(); idx++){
      WorkflowItemRequest item = parameter.get(idx);
      Object[] attributes = new Object[]{
        item.getBeforeCode(),
        item.getAfterCode(),
        item.getIsAllow(),
        item.getConditionType()
      };

      structs[idx] = conn.createStruct("WORKFLOW_OBJ", attributes);
    }

    Array arr = conn.createOracleArray("WORKFLOW_TAB", structs);
    ps.setArray(i, arr);
  }

  @Override
  public List<WorkflowItemRequest> getNullableResult(ResultSet rs, String columnName) throws SQLException {
    return List.of();
  }

  @Override
  public List<WorkflowItemRequest> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
    return List.of();
  }

  @Override
  public List<WorkflowItemRequest> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
    return List.of();
  }
}
