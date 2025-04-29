package masking.function.demo.model;

import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
@Setter
@Getter
public class TableMaskingInfo {
    private String tableName;
    private List<ColumnMasking> columns;

    public String getTableName() { return tableName; }
    public void setTableName(String tableName) { this.tableName = tableName; }
    public List<ColumnMasking> getColumns() { return columns; }
    public void setColumns(List<ColumnMasking> columns) { this.columns = columns; }
}