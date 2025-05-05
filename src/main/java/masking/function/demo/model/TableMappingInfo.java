package masking.function.demo.model;

import lombok.Getter;
import lombok.Setter;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class TableMappingInfo {
    private String tableName;
    private boolean localCm;
    private String valRules;
    private String dstUsesSrc;
    private List<ColumnMappingInfo> columns;
    private Map<String, String> columnMappings;
    private List<String> tableColumns; // Add this field
}