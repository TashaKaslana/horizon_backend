package org.phong.horizon.admin.system.database_schema.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.phong.horizon.admin.system.database_schema.dtos.DatabaseColumnDto;
import org.phong.horizon.admin.system.database_schema.dtos.DatabaseRelationshipDto;
import org.phong.horizon.admin.system.database_schema.dtos.DatabaseSchemaDto;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@AllArgsConstructor
@Service
public class DatabaseSchemaService {
    private final DataSource dataSource;

    public DatabaseSchemaDto getFullSchema() {
        DatabaseSchemaDto schema = new DatabaseSchemaDto();
        try {
            try (Connection conn = dataSource.getConnection()) {
                DatabaseMetaData metaData = conn.getMetaData();
                ResultSet tables = metaData.getTables(null, null, "%", new String[]{"TABLE"});

                while (tables.next()) {
                    String tableName = tables.getString("TABLE_NAME");

                    // --- Primary keys ---
                    Set<String> primaryKeys = new HashSet<>();
                    ResultSet pkRs = metaData.getPrimaryKeys(null, null, tableName);
                    while (pkRs.next()) {
                        primaryKeys.add(pkRs.getString("COLUMN_NAME"));
                    }

                    // --- Foreign keys with relationship info ---
                    Map<String, DatabaseRelationshipDto> foreignKeyMap = new HashMap<>();
                    ResultSet fkRs = metaData.getImportedKeys(null, null, tableName);
                    while (fkRs.next()) {
                        String fkColumn = fkRs.getString("FKCOLUMN_NAME");
                        String pkTable = fkRs.getString("PKTABLE_NAME");
                        String pkColumn = fkRs.getString("PKCOLUMN_NAME");

                        DatabaseRelationshipDto relation = new DatabaseRelationshipDto();
                        relation.setReferencedTable(pkTable);
                        relation.setReferencedColumn(pkColumn);

                        foreignKeyMap.put(fkColumn, relation);
                    }

                    // --- Column details ---
                    ResultSet columns = metaData.getColumns(null, null, tableName, "%");
                    List<DatabaseColumnDto> columnDetails = new ArrayList<>();

                    while (columns.next()) {
                        String columnName = columns.getString("COLUMN_NAME");

                        DatabaseColumnDto column = new DatabaseColumnDto();
                        column.setName(columnName);
                        column.setType(columns.getString("TYPE_NAME"));
                        column.setSize(columns.getInt("COLUMN_SIZE"));
                        column.setNullable(columns.getInt("NULLABLE") == DatabaseMetaData.columnNullable);
                        column.setIsPrimaryKey(primaryKeys.contains(columnName));

                        if (foreignKeyMap.containsKey(columnName)) {
                            column.setIsForeignKey(true);
                            column.setRelationship(foreignKeyMap.get(columnName));
                        } else {
                            column.setIsForeignKey(false);
                        }

                        columnDetails.add(column);
                    }

                    schema.getTables().put(tableName, columnDetails);
                }
            }
        } catch (Exception e) {
            log.error("Error while fetching database schema", e);
        }

        return schema;
    }
}
