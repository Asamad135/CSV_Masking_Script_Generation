package masking.function.demo.service;

import masking.function.demo.model.ColumnMasking;
import masking.function.demo.model.MaskingInfo;
import masking.function.demo.model.MaskingRequest;
import masking.function.demo.model.TableMaskingInfo;
import masking.function.demo.model.TableMeta;
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
public class MaskingScriptService {

    @Autowired
    private DatabaseUtil databaseUtil;

    public String generateMaskingScript(MaskingRequest request) throws IOException {
        // Fetch all business tables and columns
        List<TableMeta> allTables = databaseUtil.getAllBusinessTables();

        // Build payloadTables map from request
        Map<String, MaskingInfo> payloadTables = new HashMap<>();
        for (TableMaskingInfo info : request.getTables()) {
            // Convert the columns list to a map for easier lookup
            Map<String, String> columnMaskingMap = info.getColumns().stream()
                .collect(Collectors.toMap(
                    ColumnMasking::getColumnName,
                    ColumnMasking::getMaskingFunction
                ));

            // Get all columns for this table from database
            List<String> allTableColumns = allTables.stream()
                .filter(t -> t.getName().equals(info.getTableName()))
                .findFirst()
                .map(TableMeta::getColumns)
                .orElse(new ArrayList<>());

            // For columns not specified in the request, add them with identity mapping
            for (String column : allTableColumns) {
                if (!columnMaskingMap.containsKey(column)) {
                    columnMaskingMap.put(column, column); // Identity mapping
                }
            }

            MaskingInfo mi = new MaskingInfo();
            mi.setTableName(info.getTableName());
            mi.setColumnMaskingMap(columnMaskingMap);
            payloadTables.put(info.getTableName(), mi);
        }

        // Separate masked and unmasked tables
        List<TableMeta> maskedTables = allTables.stream()
                .filter(t -> payloadTables.containsKey(t.getName()))
                .collect(Collectors.toList());

        List<TableMeta> unmaskedTables = allTables.stream()
                .filter(t -> !payloadTables.containsKey(t.getName()))
                .collect(Collectors.toList());

        // Setup Velocity engine
        VelocityEngine velocityEngine = new VelocityEngine();
        velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        velocityEngine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
        velocityEngine.init();

        VelocityContext context = new VelocityContext();
        context.put("request", request);
        context.put("maskedTables", maskedTables);
        context.put("unmaskedTables", unmaskedTables);
        context.put("payloadTables", payloadTables);

        StringWriter writer = new StringWriter();
        velocityEngine.mergeTemplate("templates/masking_script.vm", "UTF-8", context, writer);

        // Write to file with timestamped name
        Path scriptDir = Paths.get("script");
        if (!Files.exists(scriptDir)) {
            Files.createDirectories(scriptDir);
        }
        String filename = "masking_script_" + System.currentTimeMillis() + ".txt";
        Path scriptFile = scriptDir.resolve(filename);
        Files.write(scriptFile, writer.toString().getBytes(StandardCharsets.UTF_8));

        return "Script generated: " + scriptFile.toAbsolutePath();
    }
}
