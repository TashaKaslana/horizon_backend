package org.phong.horizon.user.subdomain.permission.controllers;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.phong.horizon.user.subdomain.permission.dtos.CreatePermissionRequest;
import org.phong.horizon.user.subdomain.permission.dtos.PermissionDto;
import org.phong.horizon.user.subdomain.permission.dtos.UpdatePermissionRequest;
import org.phong.horizon.user.subdomain.permission.services.PermissionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users/permissions")
@AllArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;

    @PostMapping
    public ResponseEntity<PermissionDto> createPermission(@Valid @RequestBody CreatePermissionRequest request) {
        PermissionDto createdPermission = permissionService.createPermission(request);
        return new ResponseEntity<>(createdPermission, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PermissionDto> getPermissionById(@PathVariable UUID id) {
        PermissionDto permissionDto = permissionService.getPermissionById(id);
        return ResponseEntity.ok(permissionDto);
    }

    @GetMapping("/slug/{slug}")
    public ResponseEntity<PermissionDto> getPermissionBySlug(@PathVariable String slug) {
        PermissionDto permissionDto = permissionService.getPermissionBySlug(slug);
        return ResponseEntity.ok(permissionDto);
    }

    @GetMapping
    public ResponseEntity<Page<PermissionDto>> getAllPermissions(Pageable pageable) {
        Page<PermissionDto> permissions = permissionService.getAllPermissions(pageable);
        return ResponseEntity.ok(permissions);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PermissionDto> updatePermission(@PathVariable UUID id, @Valid @RequestBody UpdatePermissionRequest request) {
        PermissionDto updatedPermission = permissionService.updatePermission(id, request);
        return ResponseEntity.ok(updatedPermission);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePermission(@PathVariable UUID id) {
        permissionService.deletePermission(id);
        return ResponseEntity.noContent().build();
    }
}

