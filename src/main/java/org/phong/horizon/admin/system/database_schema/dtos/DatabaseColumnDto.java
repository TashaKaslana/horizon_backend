package org.phong.horizon.admin.system.database_schema.dtos;

import lombok.Data;

@Data
public class DatabaseColumnDto {
    private String name;
    private String type;
    private Integer size;
    private Boolean nullable;
    private Boolean isPrimaryKey;
    private Boolean isForeignKey;
    private DatabaseRelationshipDto relationship;
}
