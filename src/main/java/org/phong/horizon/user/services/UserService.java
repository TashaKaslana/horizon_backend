package org.phong.horizon.user.services;

import lombok.extern.slf4j.Slf4j;
import org.phong.horizon.infrastructure.services.AuthService;
import org.phong.horizon.user.dtos.UserCreateDto;
import org.phong.horizon.user.dtos.UserCreatedDto;
import org.phong.horizon.user.dtos.UserRespondDto;
import org.phong.horizon.user.dtos.UserSummaryRespond;
import org.phong.horizon.user.dtos.UserUpdateDto;
import org.phong.horizon.user.enums.UserErrorEnums;
import org.phong.horizon.user.exceptions.UserNotFoundException;
import org.phong.horizon.user.infrastructure.mapstruct.UserMapper;
import org.phong.horizon.user.infrastructure.persistence.entities.User;
import org.phong.horizon.user.infrastructure.persistence.repositories.UserRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class UserService {
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final AuthService authService;

    public UserService(UserMapper userMapper,
                       UserRepository userRepository,
                       AuthService authService) {
        this.userMapper = userMapper;
        this.userRepository = userRepository;
        this.authService = authService;
    }

    public UserRespondDto getCurrentUser() {
        return userMapper.toDto(authService.getUser());
    }

    @Transactional(readOnly = true)
    public UserSummaryRespond getUserSummary(UUID uuid) {
        User user = findById(uuid);

        return userMapper.toDto3(user);
    }

    public List<UserSummaryRespond> getListUserSummary() {
        List<User> users = userRepository.findAll();

        return users.stream().map(userMapper::toDto3).toList();
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN') or @authService.isPrincipal(#authentication.principal.id)")
    public void updateCurrentUser(UserUpdateDto userUpdateDto) {
        UUID userId = authService.getUserId();

        updateUser(userId, userUpdateDto);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN') or @authService.isPrincipal(#authentication.principal.id)")
    public void deleteCurrentUser() {
        UUID userId = authService.getUserId();

        deleteUser(userId);
    }

    @Transactional
    public UserCreatedDto createUser(UserCreateDto userCreateDto) {
        User user = userMapper.toEntity(userCreateDto);
        User createdUser = userRepository.save(user);

        log.info("Created user: {}", createdUser);

        return userMapper.toDto4(createdUser);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN') or @authService.isPrincipal(#uuid)")
    public UserRespondDto getUser(UUID uuid) {
        User user = findById(uuid);
        return userMapper.toDto(user);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN') or @authService.isPrincipal(#userId)")
    public void updateUser(UUID userId, UserUpdateDto userUpdateDto) {
        log.info("Attempting to update user with ID: {}", userId);

        User user = findById(userId);
        User updatedUser = userMapper.partialUpdate(userUpdateDto, user);
        User savedUser = userRepository.save(updatedUser);
        log.info("Successfully updated user with ID: {}", savedUser.getId());
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN') or @authService.isPrincipal(#uuid)")
    public void deleteUser(UUID uuid) {
        userRepository.deleteById(uuid);

        log.warn("Deleted user with id: {}", uuid);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteAllUsers() {
        userRepository.deleteAll();

        log.warn("All users have been deleted!");
    }

    @Transactional(readOnly = true)
    protected User findById(UUID uuid) {
        return userRepository.findById(uuid)
                .orElseThrow(() -> {
                    log.error("User not found with uuid: {}", uuid);
                    return new UserNotFoundException(UserErrorEnums.USER_NOT_FOUND.getMessage());
                });
    }
}