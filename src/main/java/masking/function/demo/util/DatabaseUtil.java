package masking.function.demo.util;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import masking.function.demo.model.TableMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class DatabaseUtil {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // List your business schema name here
    private static final String SCHEMA_NAME = "news_schema";

    // List of system/internal tables to exclude (add more if needed)
    private static final List<String> EXCLUDED_TABLES = Arrays.asList(
        "INNODB_BUFFER_POOL_STATS", "INNODB_CMP", "INNODB_LOCKS", "PERFORMANCE_SCHEMA", "EVENTS",
        "FILES", "USER", "HELP_TOPIC", "PROCESSLIST", "TABLES", "COLUMNS", "ROUTINES", "SCHEMATA",
        "STATISTICS", "TRIGGERS", "USER_PRIVILEGES", "ROLE_TABLE_GRANTS"
    );

    public List<TableMeta> getAllBusinessTables() {
        List<String> tableNames = jdbcTemplate.queryForList(
            "SELECT table_name FROM information_schema.tables WHERE table_schema = ? AND table_type = 'BASE TABLE'",
            String.class,
            SCHEMA_NAME
        );

        List<TableMeta> tables = new ArrayList<>();
        for (String tableName : tableNames) {
            if (EXCLUDED_TABLES.contains(tableName.toUpperCase())) {
                continue;
            }
            List<String> columns = jdbcTemplate.queryForList(
                "SELECT column_name FROM information_schema.columns WHERE table_schema = ? AND table_name = ? ORDER BY ordinal_position",
                String.class,
                SCHEMA_NAME,
                tableName
            );
            tables.add(new TableMeta(tableName, columns));
        }
        return tables;
    }
}