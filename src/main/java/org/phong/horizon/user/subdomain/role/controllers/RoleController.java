package org.phong.horizon.user.subdomain.role.controllers;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.phong.horizon.core.responses.RestApiResponse;
import org.phong.horizon.user.subdomain.role.dtos.AssignPermissionsToRoleRequest;
import org.phong.horizon.user.subdomain.role.dtos.CreateRoleRequest;
import org.phong.horizon.user.subdomain.role.dtos.RoleDto;
import org.phong.horizon.user.subdomain.role.dtos.UpdateRoleRequest;
import org.phong.horizon.user.subdomain.role.services.RoleService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/users/roles") // Base path for role management
@AllArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @PostMapping
    public ResponseEntity<RestApiResponse<RoleDto>> createRole(@Valid @RequestBody CreateRoleRequest request) {
        RoleDto createdRole = roleService.createRole(request);
        return RestApiResponse.created(createdRole);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RestApiResponse<RoleDto>> getRoleById(@PathVariable UUID id) {
        RoleDto roleDto = roleService.getRoleById(id);
        return RestApiResponse.success(roleDto);
    }

    @GetMapping("/slug/{slug}")
    public ResponseEntity<RestApiResponse<RoleDto>> getRoleBySlug(@PathVariable String slug) {
        RoleDto roleDto = roleService.getRoleBySlug(slug);
        return RestApiResponse.success(roleDto);
    }

    @GetMapping
    public ResponseEntity<RestApiResponse<List<RoleDto>>> getAllRoles(Pageable pageable) {
        Page<RoleDto> roles = roleService.getAllRoles(pageable);
        return RestApiResponse.success(roles);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RestApiResponse<RoleDto>> updateRole(@PathVariable UUID id, @Valid @RequestBody UpdateRoleRequest request) {
        RoleDto updatedRole = roleService.updateRole(id, request);
        return RestApiResponse.success(updatedRole);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<RestApiResponse<Void>> deleteRole(@PathVariable UUID id) {
        roleService.deleteRole(id);
        return RestApiResponse.noContent();
    }

    @PostMapping("/{roleId}/permissions/sync")
    public ResponseEntity<RestApiResponse<RoleDto>> syncPermissionsForRole(@PathVariable UUID roleId, @RequestBody Set<UUID> permissionIds) {
        RoleDto updatedRole = roleService.syncPermissionsForRole(roleId, permissionIds);
        return RestApiResponse.success(updatedRole);
    }

    @PostMapping("/permissions/assign")
    public ResponseEntity<RestApiResponse<RoleDto>> assignPermissionsToRole(@Valid @RequestBody AssignPermissionsToRoleRequest request) {
        RoleDto updatedRole = roleService.assignPermissionsToRole(request);
        return RestApiResponse.success(updatedRole);
    }
}

