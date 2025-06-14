package org.phong.horizon.user.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.phong.horizon.core.services.AuthService;
import org.phong.horizon.core.utils.HttpRequestUtils;
import org.phong.horizon.core.utils.ObjectHelper;
import org.phong.horizon.user.dtos.BulkUserDeleteRequest;
import org.phong.horizon.user.dtos.BulkUserUpdateRequest;
import org.phong.horizon.user.dtos.UserAccountUpdate;
import org.phong.horizon.user.dtos.UserCreateDto;
import org.phong.horizon.user.dtos.UserImageUpdate;
import org.phong.horizon.user.dtos.UserIntroduction;
import org.phong.horizon.user.dtos.UserRespondDto;
import org.phong.horizon.user.dtos.UserSummaryRespond;
import org.phong.horizon.user.dtos.UserUpdateInfoDto;
import org.phong.horizon.user.enums.UserErrorEnums;
import org.phong.horizon.user.enums.UserStatus;
import org.phong.horizon.user.events.UserAccountUpdatedEvent;
import org.phong.horizon.user.events.UserCreatedEvent;
import org.phong.horizon.user.events.UserDeletedEvent;
import org.phong.horizon.user.events.UserInfoUpdatedEvent;
import org.phong.horizon.user.events.UserRestoreEvent;
import org.phong.horizon.user.events.BulkUsersDeletedEvent;
import org.phong.horizon.user.events.BulkUsersUpdatedEvent;
import org.phong.horizon.user.exceptions.UserAlreadyExistsException;
import org.phong.horizon.user.exceptions.UserNotFoundException;
import org.phong.horizon.user.infrastructure.mapstruct.UserMapper;
import org.phong.horizon.user.infrastructure.persistence.entities.User;
import org.phong.horizon.user.infrastructure.persistence.repositories.UserRepository;
import org.phong.horizon.user.subdomain.role.entities.Role;
import org.phong.horizon.user.subdomain.role.repositories.RoleRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class UserService {
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AuthService authService;
    private final ApplicationEventPublisher publisher;

    @Transactional(readOnly = true)
    public UserRespondDto getCurrentUser() {
        return userMapper.toDto(authService.getUser());
    }

    @Transactional(readOnly = true)
    public UserSummaryRespond getUserSummary(UUID uuid) {
        User user = findById(uuid);

        return userMapper.toDto3(user);
    }

    @Transactional(readOnly = true)
    public Map<String, User> getUsersByUsernames(List<String> usernameList) {
        List<User> users = userRepository.findAllByListUserName(usernameList);

        if (users.isEmpty()) {
            log.warn("No users found with usernames: {}", usernameList);
            return null;
        }

        return users.stream()
                .map(user -> Map.entry(user.getUsername(), user))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Transactional(readOnly = true)
    public Page<UserSummaryRespond> getListUserSummary(Pageable pageable) {
        Page<User> users = userRepository.findAll(pageable);

        return users.map(userMapper::toDto3);
    }

    @Transactional
    public UserIntroduction getUserIntroduction(UUID uuid) {
        return userMapper.toDto5(findById(uuid));
    }

    @Transactional
    public Page<UserIntroduction> getUserIntroductionList(Pageable pageable) {
        Page<User> users = userRepository.findAll(pageable);
        return users.map(userMapper::toDto5);
    }

    @Transactional
    public boolean isUserExistsWithAuth0Id(String auth0Id) {
        return userRepository.existsByAuth0Id(auth0Id);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN') or @authService.isPrincipal(#authentication.principal.id)")
    public UserRespondDto updateCurrentUserInfo(UserUpdateInfoDto userUpdateInfoDto) {
        UUID userId = authService.getUserIdFromContext();

        return updateUserInfo(userId, userUpdateInfoDto);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN') or @authService.isPrincipal(#authentication.principal.id)")
    public void deleteCurrentUser() {
        UUID userId = authService.getUserIdFromContext();

        deleteUser(userId);
    }

    @Transactional
    public UserRespondDto createUser(UserCreateDto userCreateDto) {
        boolean isAlreadyExist = userRepository.isAlreadyExist(
                userCreateDto.auth0Id(), userCreateDto.username(), userCreateDto.email()
        );

        if (isAlreadyExist) {
            throw new UserAlreadyExistsException(
                    UserErrorEnums.USER_ALREADY_EXISTS.getMessage()
            );
        }

        User user = userMapper.toEntity(userCreateDto);
        if (userCreateDto.status() == null) {
            user.setStatus(UserStatus.PENDING);
        } else {
            user.setStatus(userCreateDto.status());
        }

        user.setDisplayName(userCreateDto.username());

        User createdUser = userRepository.save(user);

        log.info("Created user: {}", createdUser);

        publisher.publishEvent(new UserCreatedEvent(
                this, createdUser.getId(), createdUser.getUsername(), createdUser.getEmail()
        ));

        return userMapper.toDto(createdUser);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN') or @authService.isPrincipal(#uuid)")
    public UserRespondDto getUser(UUID uuid) {
        User user = findById(uuid);
        return userMapper.toDto(user);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN') or @authService.isPrincipal(#userId)")
    public UserRespondDto updateUserInfo(UUID userId, UserUpdateInfoDto userUpdateInfoDto) {
        log.info("Attempting to update user with ID: {}", userId);

        User original = findById(userId);
        User oldUser = userMapper.cloneUser(original);
        User temp = userMapper.partialUpdate(userUpdateInfoDto, original);

        if (userUpdateInfoDto.status() != null) {
            temp.setStatus(userUpdateInfoDto.status());
        }

        User newUser = userRepository.save(temp);

        String userAgent = Objects.requireNonNull(HttpRequestUtils.getCurrentHttpRequest()).getHeader("User-Agent");
        String clientIp = HttpRequestUtils.getClientIpAddress(HttpRequestUtils.getCurrentHttpRequest());

        publisher.publishEvent(new UserInfoUpdatedEvent(
                this, newUser.getId(), newUser.getUsername(), newUser.getEmail(),
                ObjectHelper.extractChangesWithCommonsLang(
                        userMapper.toCloneDto(oldUser),
                        userMapper.toCloneDto(newUser)
                ),
                userAgent,
                clientIp
        ));
        log.info("Successfully updated user with ID: {}", newUser.getId());

        return userMapper.toDto(newUser);
    }

    @Transactional
    public UserRespondDto updateMeAccount(UserAccountUpdate request) {
        return updateUserAccount(request, authService.getUserIdFromContext());
    }

    @Transactional
    public UserRespondDto updateUserAccount(UserAccountUpdate request, UUID uuid) {
        User user = findById(uuid);
        User oldUser = userMapper.cloneUser(user);
        User updatedUser = userMapper.partialUpdate(request, user);

        log.info("Updated user account with ID: {}", updatedUser.getId());

        String userAgent = Objects.requireNonNull(HttpRequestUtils.getCurrentHttpRequest()).getHeader("User-Agent");
        String clientIp = HttpRequestUtils.getClientIpAddress(HttpRequestUtils.getCurrentHttpRequest());

        // Use oldUser for logging changes
        publisher.publishEvent(new UserAccountUpdatedEvent(
                this, updatedUser.getId(), updatedUser.getUsername(), updatedUser.getEmail(),
                updatedUser.getProfileImage(), updatedUser.getCoverImage(), updatedUser.getBio(),
                ObjectHelper.extractChangesWithCommonsLang(
                        userMapper.toCloneDto(oldUser),
                        userMapper.toCloneDto(updatedUser)
                ),
                userAgent,
                clientIp)
        );

        return userMapper.toDto(updatedUser);
    }

    //TODO: finish
    @Transactional
    public UserRespondDto updateCurrentUserImage(UserImageUpdate request) {
        User user = findById(authService.getUserIdFromContext());
//        User oldUser = userMapper.cloneUser(user);

        User updatedUser = userMapper.partialUpdate(request, user);
//        String userAgent = Objects.requireNonNull(HttpRequestUtils.getCurrentHttpRequest()).getHeader("User-Agent");
//        String clientIp = HttpRequestUtils.getClientIpAddress(HttpRequestUtils.getCurrentHttpRequest());

        return userMapper.toDto(userRepository.save(updatedUser));
    }

//    private void updateRoles(User user, List<String> roles) {
//        log.info("Updating roles for user ID: {}", user.getId());
//        user.setRoles(roles);
//    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN') or @authService.isPrincipal(#uuid)")
    public void deleteUser(UUID uuid) {
        User userTemp = findById(uuid);
        userRepository.softDeleteById(uuid);

        log.warn("Deleted user with id: {}", uuid);
        publisher.publishEvent(new UserDeletedEvent(
                this, uuid, userTemp.getUsername(), userTemp.getEmail()
        ));
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN') or @authService.isPrincipal(#uuid)")
    public void restoreUserById(UUID uuid) {
        userRepository.restoreById(uuid);

        log.info("Restored user with id: {}", uuid);
        publisher.publishEvent(new UserRestoreEvent(
                this, uuid
        ));
    }

    //test only or admin demonstration bruh!
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteAllUsers() {
        userRepository.deleteAll();

        log.warn("All users have been deleted!");
    }

    @Transactional(readOnly = true)
    public User findById(UUID uuid) {
        return userRepository.findById(uuid)
                .orElseThrow(() -> {
                    log.error("User not found with uuid: {}", uuid);
                    return new UserNotFoundException(UserErrorEnums.USER_NOT_FOUND.getMessage());
                });
    }

    @Transactional(readOnly = true)
    public User getRefById(UUID uuid) {
        return userRepository.getReferenceById(uuid);
    }

    @Transactional
    public boolean existsById(UUID uuid) {
        return userRepository.existsById(uuid);
    }

    @Transactional
    public List<UserRespondDto> bulkUpdateUsers(BulkUserUpdateRequest req) {
        List<User> users = userRepository.findAllById(req.ids());

        Role role;
        if (req.roleId() != null) {
            role = roleRepository.getReferenceById(req.roleId());
        } else {
            role = null;
        }

        users.forEach(user -> {
            if (req.status() != null) {
                user.setStatus(req.status());
            }
            if (role != null) {
                user.setRole(role);
            }
        });

        List<User> updatedUsers = userRepository.saveAll(users);
        publisher.publishEvent(new BulkUsersUpdatedEvent(this, req.ids(), req.status(), req.roleId()));

        return updatedUsers.stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void bulkDeleteUsers(BulkUserDeleteRequest request) {
        userRepository.deleteAllById(request.userIds());
        publisher.publishEvent(new BulkUsersDeletedEvent(this, request.userIds()));
    }
}
