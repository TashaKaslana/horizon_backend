package org.phong.horizon.user.subdomain.permission.controllers;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.phong.horizon.core.responses.RestApiResponse;
import org.phong.horizon.user.subdomain.permission.dtos.CreatePermissionRequest;
import org.phong.horizon.user.subdomain.permission.dtos.PermissionDto;
import org.phong.horizon.user.subdomain.permission.dtos.UpdatePermissionRequest;
import org.phong.horizon.user.subdomain.permission.services.PermissionService;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users/permissions")
@AllArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;

    @PostMapping
    public ResponseEntity<RestApiResponse<PermissionDto>> createPermission(@Valid @RequestBody CreatePermissionRequest request) {
        PermissionDto createdPermission = permissionService.createPermission(request);
        return RestApiResponse.created(createdPermission);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RestApiResponse<PermissionDto>> getPermissionById(@PathVariable UUID id) {
        PermissionDto permissionDto = permissionService.getPermissionById(id);
        return RestApiResponse.success(permissionDto);
    }

    @GetMapping("/slug/{slug}")
    public ResponseEntity<RestApiResponse<PermissionDto>> getPermissionBySlug(@PathVariable String slug) {
        PermissionDto permissionDto = permissionService.getPermissionBySlug(slug);
        return RestApiResponse.success(permissionDto);
    }

    @GetMapping
    public ResponseEntity<RestApiResponse<List<PermissionDto>>> getAllPermissions(@ParameterObject Pageable pageable) {
        Page<PermissionDto> permissions = permissionService.getAllPermissions(pageable);
        return RestApiResponse.success(permissions);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RestApiResponse<PermissionDto>> updatePermission(@PathVariable UUID id, @Valid @RequestBody UpdatePermissionRequest request) {
        PermissionDto updatedPermission = permissionService.updatePermission(id, request);
        return RestApiResponse.success(updatedPermission);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<RestApiResponse<Void>> deletePermission(@PathVariable UUID id) {
        permissionService.deletePermission(id);
        return RestApiResponse.noContent();
    }

    @DeleteMapping("/bulk-delete")
    public ResponseEntity<RestApiResponse<Void>> bulkDeletePermissions(@Valid @RequestBody org.phong.horizon.user.subdomain.permission.dtos.BulkPermissionDeleteRequest request) {
        permissionService.bulkDeletePermissions(request);
        return RestApiResponse.noContent();
    }
}
