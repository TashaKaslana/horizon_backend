package org.phong.horizon.user.subdomain.role.controllers;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.phong.horizon.user.subdomain.role.dtos.AssignPermissionsToRoleRequest;
import org.phong.horizon.user.subdomain.role.dtos.CreateRoleRequest;
import org.phong.horizon.user.subdomain.role.dtos.RoleDto;
import org.phong.horizon.user.subdomain.role.dtos.UpdateRoleRequest;
import org.phong.horizon.user.subdomain.role.services.RoleService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/users/roles") // Base path for role management
@AllArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @PostMapping
    public ResponseEntity<RoleDto> createRole(@Valid @RequestBody CreateRoleRequest request) {
        RoleDto createdRole = roleService.createRole(request);
        return new ResponseEntity<>(createdRole, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoleDto> getRoleById(@PathVariable UUID id) {
        RoleDto roleDto = roleService.getRoleById(id);
        return ResponseEntity.ok(roleDto);
    }

    @GetMapping("/slug/{slug}")
    public ResponseEntity<RoleDto> getRoleBySlug(@PathVariable String slug) {
        RoleDto roleDto = roleService.getRoleBySlug(slug);
        return ResponseEntity.ok(roleDto);
    }

    @GetMapping
    public ResponseEntity<Page<RoleDto>> getAllRoles(Pageable pageable) {
        Page<RoleDto> roles = roleService.getAllRoles(pageable);
        return ResponseEntity.ok(roles);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoleDto> updateRole(@PathVariable UUID id, @Valid @RequestBody UpdateRoleRequest request) {
        RoleDto updatedRole = roleService.updateRole(id, request);
        return ResponseEntity.ok(updatedRole);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRole(@PathVariable UUID id) {
        roleService.deleteRole(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{roleId}/permissions/sync")
    public ResponseEntity<RoleDto> syncPermissionsForRole(@PathVariable UUID roleId, @RequestBody Set<UUID> permissionIds) {
        RoleDto updatedRole = roleService.syncPermissionsForRole(roleId, permissionIds);
        return ResponseEntity.ok(updatedRole);
    }

    @PostMapping("/permissions/assign")
    public ResponseEntity<RoleDto> assignPermissionsToRole(@Valid @RequestBody AssignPermissionsToRoleRequest request) {
        RoleDto updatedRole = roleService.assignPermissionsToRole(request);
        return ResponseEntity.ok(updatedRole);
    }
}

