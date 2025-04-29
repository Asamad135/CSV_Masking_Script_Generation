package masking.function.demo.model;

import java.util.List;





public class TableMeta {
    private String name;
    private List<String> columns;

    public TableMeta() {}

    public TableMeta(String name, List<String> columns) {
        this.name = name;
        this.columns = columns;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public List<String> getColumns() { return columns; }
    public void setColumns(List<String> columns) { this.columns = columns; }
}
