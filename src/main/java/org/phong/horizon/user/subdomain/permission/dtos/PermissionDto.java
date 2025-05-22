package org.phong.horizon.user.subdomain.permission.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PermissionDto {
    private UUID id;
    private String name;
    private String slug;
    private String description;
    private String module;
}

