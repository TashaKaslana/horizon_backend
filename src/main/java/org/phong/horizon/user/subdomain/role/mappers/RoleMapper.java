package org.phong.horizon.user.subdomain.role.mappers;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.phong.horizon.user.subdomain.permission.mapppers.PermissionMapper;
import org.phong.horizon.user.subdomain.role.dtos.CreateRoleRequest;
import org.phong.horizon.user.subdomain.role.dtos.RoleDto;
import org.phong.horizon.user.subdomain.role.dtos.UpdateRoleRequest;
import org.phong.horizon.user.subdomain.permission.entities.Permission;
import org.phong.horizon.user.subdomain.role.entities.Role;
import org.phong.horizon.user.subdomain.role.entities.RolePermission;
import org.phong.horizon.user.subdomain.permission.dtos.PermissionDto;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {PermissionMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RoleMapper {

    @Mapping(target = "permissions", source = "rolePermissions")
    RoleDto toDto(Role role);

    // Custom mapping for Set<RolePermission> to Set<PermissionDto>
    // This is automatically handled if PermissionMapper is correctly set up and RolePermission has a getPermission() method.
    // For clarity, if PermissionMapper maps Permission to PermissionDto, this should work:
    // default Set<PermissionDto> mapRolePermissionsToPermissionDtos(Set<RolePermission> rolePermissions) {
    //    if (rolePermissions == null) return null;
    //    PermissionMapper permissionMapper = Mappers.getMapper(PermissionMapper.class); // Or inject if Spring managed
    //    return rolePermissions.stream()
    //        .map(RolePermission::getPermission)
    //        .map(permissionMapper::toDto)
    //        .collect(Collectors.toSet());
    // }

    @Mapping(target = "rolePermissions", ignore = true) // Handled in service layer
    Role toEntity(CreateRoleRequest createRoleRequest);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "rolePermissions", ignore = true) // Permissions are managed separately
    void updateEntityFromRequest(UpdateRoleRequest updateRoleRequest, @MappingTarget Role role);

    // Helper to map Set<UUID> to Set<Permission> - typically done in service or with a custom provider in mapper
    // For simplicity, this example assumes the service will handle fetching Permission entities by IDs.
    default Set<Permission> mapPermissionIdsToPermissions(Set<UUID> permissionIds) {
        if (permissionIds == null) {
            return null;
        }
        // This would require a PermissionRepository or service to fetch actual Permission entities
        // For now, returning null or an empty set, actual fetching should be in the service.
        return permissionIds.stream().map(id -> {
            Permission p = new Permission();
            p.setId(id);
            return p;
        }).collect(Collectors.toSet());
    }

    // Helper to map Set<RolePermission> to Set<PermissionDto>
    // This is a more explicit way if PermissionMapper is available
    default Set<PermissionDto> mapPermissions(Set<RolePermission> value) {
        if (value == null) {
            return null;
        }
        // Assuming PermissionMapper is injected or accessible
        // This is a simplified version. In a real scenario, you'd use the injected PermissionMapper.
        return value.stream()
                .map(RolePermission::getPermission)
                .map(permission -> {
                    PermissionDto dto = new PermissionDto();
                    dto.setId(permission.getId());
                    dto.setName(permission.getName());
                    dto.setSlug(permission.getSlug());
                    dto.setDescription(permission.getDescription());
                    dto.setModule(permission.getModule());
                    return dto;
                })
                .collect(Collectors.toSet());
    }
}

