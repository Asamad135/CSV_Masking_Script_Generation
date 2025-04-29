package masking.function.demo.service;

import masking.function.demo.model.ColumnMasking;
import masking.function.demo.model.MaskingInfo;
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

    public String generateMaskingScript(List<TableMaskingInfo> maskingInfos) throws IOException {
        // Fetch all business tables and columns
        List<TableMeta> allTables = databaseUtil.getAllBusinessTables();

        // Build payloadTables map: tableName -> MaskingInfo (column -> maskingFunction)
        Map<String, MaskingInfo> payloadTables = new HashMap<>();
        for (TableMaskingInfo info : maskingInfos) {
            Map<String, String> columnMaskingMap = new HashMap<>();
            for (ColumnMasking cm : info.getColumns()) {
                columnMaskingMap.put(cm.getColumnName(), cm.getMaskingFunction());
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
        context.put("allTables", allTables);

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
