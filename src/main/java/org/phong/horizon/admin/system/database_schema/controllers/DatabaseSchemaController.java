package org.phong.horizon.admin.system.database_schema.controllers;

import lombok.AllArgsConstructor;
import org.phong.horizon.admin.system.database_schema.dtos.DatabaseSchemaDto;
import org.phong.horizon.admin.system.database_schema.services.DatabaseSchemaService;
import org.phong.horizon.core.responses.RestApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/admin/system/database-schema")
public class DatabaseSchemaController {
    private final DatabaseSchemaService databaseSchemaService;

    @GetMapping
    public ResponseEntity<RestApiResponse<DatabaseSchemaDto>> getDatabaseSchema() {
        return RestApiResponse.success(databaseSchemaService.getFullSchema());
    }
}
