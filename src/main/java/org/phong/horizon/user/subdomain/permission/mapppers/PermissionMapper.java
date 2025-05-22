package org.phong.horizon.user.subdomain.permission.mapppers;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.phong.horizon.user.subdomain.permission.dtos.CreatePermissionRequest;
import org.phong.horizon.user.subdomain.permission.dtos.PermissionDto;
import org.phong.horizon.user.subdomain.permission.dtos.UpdatePermissionRequest;
import org.phong.horizon.user.subdomain.permission.entities.Permission;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PermissionMapper {

    PermissionDto toDto(Permission permission);

    Permission toEntity(CreatePermissionRequest createPermissionRequest);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromRequest(UpdatePermissionRequest updatePermissionRequest, @MappingTarget Permission permission);
}

