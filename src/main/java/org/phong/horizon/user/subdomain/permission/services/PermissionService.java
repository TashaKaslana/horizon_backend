package org.phong.horizon.user.subdomain.permission.services;

import lombok.AllArgsConstructor;
import org.phong.horizon.user.subdomain.permission.dtos.CreatePermissionRequest;
import org.phong.horizon.user.subdomain.permission.dtos.PermissionDto;
import org.phong.horizon.user.subdomain.permission.dtos.UpdatePermissionRequest;
import org.phong.horizon.user.subdomain.permission.entities.Permission;
import org.phong.horizon.user.subdomain.permission.enums.PermissionExceptionMessage;
import org.phong.horizon.user.subdomain.permission.exceptions.PermissionAlreadyExistsException;
import org.phong.horizon.user.subdomain.permission.exceptions.PermissionManagementException;
import org.phong.horizon.user.subdomain.permission.exceptions.PermissionNotFoundException;
import org.phong.horizon.user.subdomain.permission.mapppers.PermissionMapper;
import org.phong.horizon.user.subdomain.permission.repositories.PermissionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PermissionService {

    private static final Logger log = LoggerFactory.getLogger(PermissionService.class);

    private final PermissionRepository permissionRepository;
    private final PermissionMapper permissionMapper;

    @Transactional
    public PermissionDto createPermission(CreatePermissionRequest request) {
        log.info("Attempting to create permission with slug: {}", request.getSlug());
        if (permissionRepository.existsBySlug(request.getSlug())) {
            throw new PermissionAlreadyExistsException(PermissionExceptionMessage.PERMISSION_ALREADY_EXISTS_SLUG, request.getSlug());
        }
        if (permissionRepository.existsByName(request.getName())) {
            throw new PermissionAlreadyExistsException(PermissionExceptionMessage.PERMISSION_ALREADY_EXISTS_NAME, request.getName());
        }

        Permission permission = permissionMapper.toEntity(request);
        try {
            Permission savedPermission = permissionRepository.save(permission);
            log.info("Successfully created permission with ID: {} and slug: {}", savedPermission.getId(), savedPermission.getSlug());
            return permissionMapper.toDto(savedPermission);
        } catch (DataIntegrityViolationException e) {
            log.error("Data integrity violation while creating permission with slug: {}", request.getSlug(), e);
            // This generic exception can be a fallback or customized further
            throw new PermissionManagementException("Could not create permission due to data integrity violation.", e);
        }
    }

    @Transactional(readOnly = true)
    public PermissionDto getPermissionById(UUID id) {
        log.debug("Fetching permission by ID: {}", id);
        return permissionRepository.findById(id)
                .map(permissionMapper::toDto)
                .orElseThrow(() -> new PermissionNotFoundException(PermissionExceptionMessage.PERMISSION_NOT_FOUND, id));
    }

    @Transactional(readOnly = true)
    public PermissionDto getPermissionBySlug(String slug) {
        log.debug("Fetching permission by slug: {}", slug);
        return permissionRepository.findBySlug(slug)
                .map(permissionMapper::toDto)
                .orElseThrow(() -> new PermissionNotFoundException(PermissionExceptionMessage.PERMISSION_BY_SLUG_NOT_FOUND, slug));
    }

    @Transactional(readOnly = true)
    public Page<PermissionDto> getAllPermissions(Pageable pageable) {
        log.debug("Fetching all permissions for page: {} and size: {}", pageable.getPageNumber(), pageable.getPageSize());
        return permissionRepository.findAll(pageable)
                .map(permissionMapper::toDto);
    }

    @Transactional
    public PermissionDto updatePermission(UUID id, UpdatePermissionRequest request) {
        log.info("Attempting to update permission with ID: {}", id);
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new PermissionNotFoundException(PermissionExceptionMessage.PERMISSION_NOT_FOUND, id));

        if (request.getName() != null && !request.getName().equals(permission.getName()) && permissionRepository.existsByName(request.getName())) {
            throw new PermissionAlreadyExistsException(PermissionExceptionMessage.PERMISSION_ALREADY_EXISTS_NAME, request.getName());
        }

        permissionMapper.updateEntityFromRequest(request, permission);

        try {
            Permission updatedPermission = permissionRepository.save(permission);
            log.info("Successfully updated permission with ID: {}", updatedPermission.getId());
            return permissionMapper.toDto(updatedPermission);
        } catch (DataIntegrityViolationException e) {
            log.error("Data integrity violation while updating permission with ID: {}", id, e);
            throw new PermissionManagementException("Could not update permission due to data integrity violation.", e);
        }
    }

    @Transactional
    public void deletePermission(UUID id) {
        log.info("Attempting to delete permission with ID: {}", id);
        if (!permissionRepository.existsById(id)) {
            throw new PermissionNotFoundException(PermissionExceptionMessage.PERMISSION_NOT_FOUND, id);
        }
        permissionRepository.deleteById(id);
        log.info("Successfully deleted permission with ID: {}", id);
    }

    @Transactional(readOnly = true)
    public List<Permission> findAllByIds(Set<UUID> ids) {
        log.debug("Fetching permissions by IDs: {}", ids);
        List<Permission> permissions = permissionRepository.findAllById(ids);
        if (permissions.size() != ids.size()) {
            Set<UUID> foundIds = permissions.stream().map(Permission::getId).collect(Collectors.toSet());
            Set<UUID> notFoundIds = ids.stream().filter(id -> !foundIds.contains(id)).collect(Collectors.toSet());
            log.warn("Some permissions not found by IDs. Missing: {}", notFoundIds);

            throw new PermissionNotFoundException(PermissionExceptionMessage.PERMISSIONS_NOT_FOUND_BY_IDS, notFoundIds.toString());
        }
        return permissions;
    }

    @Transactional(readOnly = true)
    public Permission findById(UUID id) {
        return permissionRepository.findById(id)
                .orElseThrow(() -> new PermissionNotFoundException(PermissionExceptionMessage.PERMISSION_NOT_FOUND, id));

    }
}
