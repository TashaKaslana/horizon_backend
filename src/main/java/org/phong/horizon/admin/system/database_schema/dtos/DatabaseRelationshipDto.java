package org.phong.horizon.admin.system.database_schema.dtos;

import lombok.Data;

@Data
public class DatabaseRelationshipDto {
    private String referencedTable;
    private String referencedColumn;
}
