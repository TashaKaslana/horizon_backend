package org.phong.horizon.admin.system.database_schema.dtos;

import lombok.Data;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;

@Data
public class DatabaseSchemaDto {
    private Map<String, List<DatabaseColumnDto>> tables = new LinkedHashMap<>();
}
