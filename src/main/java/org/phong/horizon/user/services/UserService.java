package org.phong.horizon.user.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.phong.horizon.core.services.AuthService;
import org.phong.horizon.core.utils.ObjectHelper;
import org.phong.horizon.user.dtos.UserAccountUpdate;
import org.phong.horizon.user.dtos.UserCreateDto;
import org.phong.horizon.user.dtos.UserCreatedDto;
import org.phong.horizon.user.dtos.UserRespondDto;
import org.phong.horizon.user.dtos.UserSummaryRespond;
import org.phong.horizon.user.dtos.UserUpdateInfoDto;
import org.phong.horizon.user.enums.UserErrorEnums;
import org.phong.horizon.user.events.UserAccountUpdatedEvent;
import org.phong.horizon.user.events.UserCreatedEvent;
import org.phong.horizon.user.events.UserDeletedEvent;
import org.phong.horizon.user.events.UserRestoreEvent;
import org.phong.horizon.user.events.UserInfoUpdatedEvent;
import org.phong.horizon.user.exceptions.UserAlreadyExistsException;
import org.phong.horizon.user.exceptions.UserNotFoundException;
import org.phong.horizon.user.infrastructure.mapstruct.UserMapper;
import org.phong.horizon.user.infrastructure.persistence.entities.User;
import org.phong.horizon.user.infrastructure.persistence.repositories.UserRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class UserService {
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final AuthService authService;
    private final ApplicationEventPublisher publisher;

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

    public Page<UserSummaryRespond> getListUserSummary(Pageable pageable) {
        Page<User> users = userRepository.findAll(pageable);

        return users.map(userMapper::toDto3);
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
    public UserCreatedDto createUser(UserCreateDto userCreateDto) {
        boolean isAlreadyExist = userRepository.isAlreadyExist(
                userCreateDto.auth0Id(), userCreateDto.username(), userCreateDto.email()
        );

        if (isAlreadyExist) {
            throw new UserAlreadyExistsException(
                    UserErrorEnums.USER_ALREADY_EXISTS.getMessage()
            );
        }

        User user = userMapper.toEntity(userCreateDto);
        User createdUser = userRepository.save(user);

        log.info("Created user: {}", createdUser);

        publisher.publishEvent(new UserCreatedEvent(
                this, createdUser.getId(), createdUser.getUsername(), createdUser.getEmail()
        ));

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
    public UserRespondDto updateUserInfo(UUID userId, UserUpdateInfoDto userUpdateInfoDto) {
        log.info("Attempting to update user with ID: {}", userId);

        User original = findById(userId);
        User oldUser = userMapper.cloneUser(original);
        User temp = userMapper.partialUpdate(userUpdateInfoDto, original);

//        if (userUpdateDto.username() != null) {
//            updateUsername(temp, userUpdateDto.username());
//        }
//
//        if (userUpdateDto.email() != null) {
//            updateEmail(temp, userUpdateDto.email());
//        }

//        if (userUpdateDto.getRoles() != null) {
//            updateRoles(user, userUpdateDto.getRoles());
//        }

        User newUser = userRepository.save(temp);

        publisher.publishEvent(new UserInfoUpdatedEvent(
                this, newUser.getId(), newUser.getUsername(), newUser.getEmail(),
                ObjectHelper.extractChangesWithCommonsLang(
                        userMapper.toCloneDto(oldUser),
                        userMapper.toCloneDto(newUser)
                )
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

        publisher.publishEvent(new UserAccountUpdatedEvent(
                this, updatedUser.getId(), updatedUser.getUsername(), updatedUser.getEmail(),
                updatedUser.getProfileImage(), updatedUser.getCoverImage(), updatedUser.getBio(),
                ObjectHelper.extractChangesWithCommonsLang(
                        userMapper.toCloneDto(oldUser),
                        userMapper.toCloneDto(updatedUser)
                ))
        );

        return userMapper.toDto(updatedUser);
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
}