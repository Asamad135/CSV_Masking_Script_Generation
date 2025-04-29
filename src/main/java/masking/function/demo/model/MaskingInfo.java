package masking.function.demo.model;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;
@Setter
@Getter
public class MaskingInfo {
    private String tableName;
    private Map<String, String> columnMaskingMap;

    public String getTableName() { return tableName; }
    public void setTableName(String tableName) { this.tableName = tableName; }
    public Map<String, String> getColumnMaskingMap() { return columnMaskingMap; }
    public void setColumnMaskingMap(Map<String, String> columnMaskingMap) { this.columnMaskingMap = columnMaskingMap; }
}