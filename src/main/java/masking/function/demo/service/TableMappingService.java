package masking.function.demo.service;

import masking.function.demo.model.*;
import masking.function.demo.util.DatabaseUtil;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TableMappingService {

    @Autowired
    private DatabaseUtil databaseUtil;

    public String generateTableMappingScript(TableMappingRequest request) throws IOException {
        List<TableMeta> allTables = databaseUtil.getAllBusinessTables();
        
        // Convert request tables to map and populate all columns
        Map<String, TableMappingInfo> mappingTables = new LinkedHashMap<>();
        for (TableMappingInfo table : request.getTables()) {
            // Get all columns for this table
            TableMeta tableMeta = allTables.stream()
                .filter(t -> t.getName().equalsIgnoreCase(table.getTableName()))
                .findFirst()
                .orElse(null);

            if (tableMeta != null && table.isLocalCm()) {
                // Create mapping for all columns
                Map<String, String> fullColumnMappings = new LinkedHashMap<>();
                List<String> tableColumns = tableMeta.getColumns();
                
                // First add all columns with identity mapping
                for (String col : tableColumns) {
                    fullColumnMappings.put(col.toUpperCase(), col);
                }
                
                // Then override with specified mappings
                if (table.getColumns() != null) {
                    for (ColumnMappingInfo col : table.getColumns()) {
                        String columnName = col.getColumnName().toUpperCase();
                        if (tableColumns.stream().anyMatch(tc -> tc.equalsIgnoreCase(columnName))) {
                            fullColumnMappings.put(columnName, col.getMappingFunction());
                        }
                    }
                }
                
                table.setColumnMappings(fullColumnMappings);
                table.setTableColumns(tableColumns); // Add all columns to the table info
            }
            mappingTables.put(table.getTableName().toUpperCase(), table);
        }

        // Setup Velocity context
        VelocityContext context = new VelocityContext();
        context.put("request", request);
        context.put("allTables", allTables);
        context.put("mappingTables", mappingTables);

        // Generate script
        VelocityEngine velocityEngine = new VelocityEngine();
        velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        velocityEngine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
        velocityEngine.init();

        StringWriter writer = new StringWriter();
        velocityEngine.mergeTemplate("templates/table_mapping.vm", "UTF-8", context, writer);

        // Save to file
        Path scriptDir = Paths.get("table-mapping-script");
        if (!Files.exists(scriptDir)) {
            Files.createDirectories(scriptDir);
        }
        String filename = "table_mapping_script_" + System.currentTimeMillis() + ".txt";
        Path scriptFile = scriptDir.resolve(filename);
        Files.write(scriptFile, writer.toString().getBytes(StandardCharsets.UTF_8));

        return "Table mapping script generated successfully: " + filename;
    }
}