package org.phong.horizon.user.subdomain.role.services;

import lombok.AllArgsConstructor;
import org.phong.horizon.user.subdomain.permission.entities.Permission;
import org.phong.horizon.user.subdomain.permission.services.PermissionService;
import org.phong.horizon.user.subdomain.role.dtos.AssignPermissionsToRoleRequest;
import org.phong.horizon.user.subdomain.role.dtos.CreateRoleRequest;
import org.phong.horizon.user.subdomain.role.dtos.RoleDto;
import org.phong.horizon.user.subdomain.role.dtos.UpdateRoleRequest;
import org.phong.horizon.user.subdomain.role.entities.Role;
import org.phong.horizon.user.subdomain.role.entities.RolePermission;
import org.phong.horizon.user.subdomain.role.enums.RoleExceptionMessage;
import org.phong.horizon.user.subdomain.role.exceptions.PermissionNotFoundForRoleException;
import org.phong.horizon.user.subdomain.role.exceptions.RoleAlreadyExistsException;
import org.phong.horizon.user.subdomain.role.exceptions.RoleManagementException;
import org.phong.horizon.user.subdomain.role.exceptions.RoleNotFoundException;
import org.phong.horizon.user.subdomain.role.mappers.RoleMapper;
import org.phong.horizon.user.subdomain.role.repositories.RolePermissionRepository;
import org.phong.horizon.user.subdomain.role.repositories.RoleRepository;
import org.phong.horizon.user.infrastructure.persistence.repositories.UserRepository; // For checking if role is in use
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class RoleService {

    private static final Logger log = LoggerFactory.getLogger(RoleService.class);

    private final RoleRepository roleRepository;
    private final PermissionService permissionService;
    private final RolePermissionRepository rolePermissionRepository;
    private final RoleMapper roleMapper;
    private final UserRepository userRepository; // To check if role is assigned to any user

    @Transactional
    public RoleDto createRole(CreateRoleRequest request) {
        log.info("Attempting to create role with slug: {}", request.getSlug());
        if (roleRepository.existsBySlug(request.getSlug())) {
            throw new RoleAlreadyExistsException(RoleExceptionMessage.ROLE_ALREADY_EXISTS_SLUG, request.getSlug());
        }
        if (roleRepository.existsByName(request.getName())) {
            throw new RoleAlreadyExistsException(RoleExceptionMessage.ROLE_ALREADY_EXISTS_NAME, request.getName());
        }

        Role role = roleMapper.toEntity(request);
        try {
            Role savedRole = roleRepository.save(role);
            log.info("Successfully created role with ID: {} and slug: {}", savedRole.getId(), savedRole.getSlug());

            // Assign permissions if provided
            if (request.getPermissionIds() != null && !request.getPermissionIds().isEmpty()) {
                assignPermissionsToRoleInternal(savedRole, request.getPermissionIds());
            }
            // Fetch again to get role with permissions for DTO mapping
            Role roleWithPermissions = roleRepository.findById(savedRole.getId())
                 .orElseThrow(() -> new RoleNotFoundException(RoleExceptionMessage.ROLE_NOT_FOUND, savedRole.getId()));
            return roleMapper.toDto(roleWithPermissions);
        } catch (DataIntegrityViolationException e) {
            log.error("Data integrity violation while creating role with slug: {}", request.getSlug(), e);
            throw new RoleManagementException("Could not create role due to data integrity violation (e.g., slug or name might already exist).", e);
        }
    }

    @Transactional(readOnly = true)
    public RoleDto getRoleById(UUID id) {
        log.debug("Fetching role by ID: {}", id);
        return roleRepository.findById(id)
                .map(roleMapper::toDto)
                .orElseThrow(() -> new RoleNotFoundException(RoleExceptionMessage.ROLE_NOT_FOUND, id));
    }

    @Transactional(readOnly = true)
    public RoleDto getRoleBySlug(String slug) {
        log.debug("Fetching role by slug: {}", slug);
        return roleRepository.findBySlug(slug)
                .map(roleMapper::toDto)
                .orElseThrow(() -> new RoleNotFoundException(RoleExceptionMessage.ROLE_BY_SLUG_NOT_FOUND, slug));
    }

    @Transactional(readOnly = true)
    public Page<RoleDto> getAllRoles(Pageable pageable) {
        log.debug("Fetching all roles for page: {} and size: {}", pageable.getPageNumber(), pageable.getPageSize());
        return roleRepository.findAll(pageable)
                .map(roleMapper::toDto);
    }

    @Transactional
    public RoleDto updateRole(UUID id, UpdateRoleRequest request) {
        log.info("Attempting to update role with ID: {}", id);
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RoleNotFoundException(RoleExceptionMessage.ROLE_NOT_FOUND, id));

        if (request.getName() != null && !request.getName().equals(role.getName()) && roleRepository.existsByName(request.getName())) {
            throw new RoleAlreadyExistsException(RoleExceptionMessage.ROLE_ALREADY_EXISTS_NAME, request.getName());
        }

        roleMapper.updateEntityFromRequest(request, role);

        try {
            Role updatedRole = roleRepository.save(role);
            log.info("Successfully updated role with ID: {}", updatedRole.getId());
            return roleMapper.toDto(updatedRole);
        } catch (DataIntegrityViolationException e) {
            log.error("Data integrity violation while updating role with ID: {}", id, e);
            throw new RoleManagementException("Could not update role due to data integrity violation.", e);
        }
    }

    @Transactional
    public void deleteRole(UUID id) {
        log.info("Attempting to delete role with ID: {}", id);
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RoleNotFoundException(RoleExceptionMessage.ROLE_NOT_FOUND, id));

        // Check if the role is assigned to any users
        if (userRepository.existsByRoleId(id)) {
             throw new RoleManagementException(RoleExceptionMessage.CANNOT_DELETE_ROLE_HAS_USERS.format(role.getName()));
        }

        // Remove all permissions associated with this role first
        rolePermissionRepository.deleteByRoleId(id);
        roleRepository.delete(role);
        log.info("Successfully deleted role with ID: {}", id);
    }

    @Transactional
    public RoleDto assignPermissionsToRole(AssignPermissionsToRoleRequest request) {
        log.info("Attempting to assign permissions to role ID: {}", request.getRoleId());
        Role role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new RoleNotFoundException(RoleExceptionMessage.ROLE_NOT_FOUND, request.getRoleId()));

        assignPermissionsToRoleInternal(role, request.getPermissionIds());

        // Fetch again to get role with updated permissions for DTO mapping
        Role updatedRole = roleRepository.findById(role.getId())
             .orElseThrow(() -> new RoleNotFoundException(RoleExceptionMessage.ROLE_NOT_FOUND, role.getId()));
        return roleMapper.toDto(updatedRole);
    }

    private void assignPermissionsToRoleInternal(Role role, Set<UUID> permissionIds) {
        if (permissionIds == null || permissionIds.isEmpty()) {
            log.info("No permission IDs provided for role ID: {}. Skipping assignment.", role.getId());
            // Optionally, clear existing permissions if this is meant to be an exclusive set
            // rolePermissionRepository.deleteByRoleId(role.getId());
            return;
        }

        List<Permission> permissionsToAssign = permissionService.findAllByIds(permissionIds);
        if (permissionsToAssign.size() != permissionIds.size()) {
            Set<UUID> foundPermissionIds = permissionsToAssign.stream().map(Permission::getId).collect(Collectors.toSet());
            Set<UUID> notFoundIds = new HashSet<>(permissionIds);
            notFoundIds.removeAll(foundPermissionIds);
            log.warn("Some permissions not found for assignment to role ID: {}. Missing IDs: {}", role.getId(), notFoundIds);
            throw new PermissionNotFoundForRoleException(RoleExceptionMessage.PERMISSION_NOT_FOUND_FOR_ROLE, notFoundIds.toString());
        }

        Set<RolePermission> newRolePermissions = new HashSet<>();
        for (Permission permission : permissionsToAssign) {
            if (!rolePermissionRepository.existsByRoleIdAndPermissionId(role.getId(), permission.getId())) {
                RolePermission rolePermission = new RolePermission();
                rolePermission.setRole(role);
                rolePermission.setPermission(permission);
                newRolePermissions.add(rolePermission);
            }
        }
        rolePermissionRepository.saveAll(newRolePermissions);
        log.info("Successfully assigned/updated {} permissions for role ID: {}", newRolePermissions.size(), role.getId());
    }


    @Transactional
    public RoleDto syncPermissionsForRole(UUID roleId, Set<UUID> newPermissionIds) {
        log.info("Attempting to synchronize permissions for role ID: {}", roleId);
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RoleNotFoundException(RoleExceptionMessage.ROLE_NOT_FOUND, roleId));

        if (newPermissionIds != null && !newPermissionIds.isEmpty()) {
            List<Permission> permissionsToAssign = permissionService.findAllByIds(newPermissionIds);
            if (permissionsToAssign.size() != newPermissionIds.size()) {
                Set<UUID> foundPermissionIds = permissionsToAssign.stream().map(Permission::getId).collect(Collectors.toSet());
                Set<UUID> notFoundIds = new HashSet<>(newPermissionIds);
                notFoundIds.removeAll(foundPermissionIds);
                log.warn("Some permissions not found for synchronization with role ID: {}. Missing IDs: {}", roleId, notFoundIds);
                throw new PermissionNotFoundForRoleException(RoleExceptionMessage.PERMISSION_NOT_FOUND_FOR_ROLE, notFoundIds.toString());
            }
        } else {
             // If newPermissionIds is null or empty, remove all existing permissions
            log.info("Empty permission set provided for role ID: {}. Removing all existing permissions.", roleId);
            rolePermissionRepository.deleteByRoleId(roleId);
            Role updatedRole = roleRepository.findById(roleId)
                .orElseThrow(() -> new RoleNotFoundException(RoleExceptionMessage.ROLE_NOT_FOUND, roleId)); // Re-fetch
            return roleMapper.toDto(updatedRole);
        }

        rolePermissionRepository.deleteByRoleIdAndPermissionIdNotIn(roleId, newPermissionIds);

        Set<RolePermission> newAssignments = new HashSet<>();
        for (UUID permissionId : newPermissionIds) {
            if (!rolePermissionRepository.existsByRoleIdAndPermissionId(roleId, permissionId)) {
                Permission permission = permissionService.findById(permissionId);
                RolePermission rolePermission = new RolePermission();
                rolePermission.setRole(role);
                rolePermission.setPermission(permission);
                newAssignments.add(rolePermission);
            }
        }
        if (!newAssignments.isEmpty()) {
            rolePermissionRepository.saveAll(newAssignments);
        }

        log.info("Successfully synchronized permissions for role ID: {}", roleId);
        Role updatedRole = roleRepository.findById(roleId)
             .orElseThrow(() -> new RoleNotFoundException(RoleExceptionMessage.ROLE_NOT_FOUND, roleId));
        return roleMapper.toDto(updatedRole);
    }
}

