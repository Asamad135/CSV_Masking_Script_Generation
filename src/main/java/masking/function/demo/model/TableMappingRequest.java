package masking.function.demo.model;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class TableMappingRequest {
    private String tmName;
    private String srcQual;
    private String destQual;
    private String srcExt;
    private String srcType;
    private String destDb;
    private String destType;
    private String valRules;
    private String unusedObj;
    private String solution;
    private List<TableMappingInfo> tables;
    private List<String> procedures;
}