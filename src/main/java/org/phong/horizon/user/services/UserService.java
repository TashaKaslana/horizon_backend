package org.phong.horizon.user.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.phong.horizon.core.services.AuthService;
import org.phong.horizon.core.utils.ObjectHelper;
import org.phong.horizon.user.dtos.UserCreateDto;
import org.phong.horizon.user.dtos.UserCreatedDto;
import org.phong.horizon.user.dtos.UserRespondDto;
import org.phong.horizon.user.dtos.UserSummaryRespond;
import org.phong.horizon.user.dtos.UserUpdateDto;
import org.phong.horizon.user.enums.UserErrorEnums;
import org.phong.horizon.user.events.UserCreatedEvent;
import org.phong.horizon.user.events.UserDeletedEvent;
import org.phong.horizon.user.events.UserUpdatedEvent;
import org.phong.horizon.user.exceptions.UserNotFoundException;
import org.phong.horizon.user.infrastructure.mapstruct.UserMapper;
import org.phong.horizon.user.infrastructure.persistence.entities.User;
import org.phong.horizon.user.infrastructure.persistence.repositories.UserRepository;
import org.springframework.context.ApplicationEventPublisher;
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

    public List<UserSummaryRespond> getListUserSummary() {
        List<User> users = userRepository.findAll();

        return users.stream().map(userMapper::toDto3).toList();
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN') or @authService.isPrincipal(#authentication.principal.id)")
    public void updateCurrentUser(UserUpdateDto userUpdateDto) {
        UUID userId = authService.getUserIdFromContext();

        updateUser(userId, userUpdateDto);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN') or @authService.isPrincipal(#authentication.principal.id)")
    public void deleteCurrentUser() {
        UUID userId = authService.getUserIdFromContext();

        deleteUser(userId);
    }

    @Transactional
    public UserCreatedDto createUser(UserCreateDto userCreateDto) {
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


    //TODO: update this again
    @Transactional
    @PreAuthorize("hasRole('ADMIN') or @authService.isPrincipal(#userId)")
    public void updateUser(UUID userId, UserUpdateDto userUpdateDto) {
        log.info("Attempting to update user with ID: {}", userId);

        User oldUser = findById(userId);
        User temp = userMapper.partialUpdate( userUpdateDto, oldUser);

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

        publisher.publishEvent(new UserUpdatedEvent(
                this, newUser.getId(), newUser.getUsername(), newUser.getEmail(),
                ObjectHelper.extractChangesWithCommonsLang(oldUser, newUser)
        ));
        log.info("Successfully updated user with ID: {}", newUser.getId());
    }


    private void updateUsername(User user, String username) {
        log.info("Updating username for user ID: {}", user.getId());
        user.setUsername(username);
    }

    private void updateEmail(User user, String email) {
        log.info("Updating email for user ID: {}", user.getId());
        user.setEmail(email);
    }

//    private void updateRoles(User user, List<String> roles) {
//        log.info("Updating roles for user ID: {}", user.getId());
//        user.setRoles(roles);
//    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN') or @authService.isPrincipal(#uuid)")
    public void deleteUser(UUID uuid) {
        User userTemp = findById(uuid);
        userRepository.deleteById(uuid);

        log.warn("Deleted user with id: {}", uuid);
        publisher.publishEvent(new UserDeletedEvent(
                this, uuid, userTemp.getUsername(), userTemp.getEmail()
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