package org.phong.horizon.user.services;

import org.phong.horizon.infrastructure.services.AuthService;
import org.phong.horizon.user.dtos.UserCreateDto;
import org.phong.horizon.user.dtos.UserRespondDto;
import org.phong.horizon.user.dtos.UserUpdateDto;
import org.phong.horizon.user.enums.UserErrorEnums;
import org.phong.horizon.user.exceptions.UserNotFoundException;
import org.phong.horizon.user.infrastructure.mapstruct.UserMapper;
import org.phong.horizon.user.infrastructure.persistence.entities.User;
import org.phong.horizon.user.infrastructure.persistence.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserService {
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final AuthService authService;

    public UserService(UserMapper userMapper, UserRepository userRepository, AuthService authService) {
        this.userMapper = userMapper;
        this.userRepository = userRepository;
        this.authService = authService;
    }

    public UserRespondDto getCurrentUser() {
        User user = userRepository.findByAuth0Id(authService.getUserId());

        return getUser(user.getId());
    }

    public void updateCurrentUser(UserUpdateDto userUpdateDto) {
        UUID userId = UUID.fromString(authService.getUserId());
        updateUser(userId, userUpdateDto);
    }

    public void deleteCurrentUser() {
        UUID userId = UUID.fromString(authService.getUserId());
        deleteUser(userId);
    }

    public UUID createUser(UserCreateDto userCreateDto) {
        User user = userMapper.toEntity(userCreateDto);
        User createdUser = userRepository.save(user);

        return createdUser.getId();
    }

    public UserRespondDto getUser(UUID uuid) {
        User user = findById(uuid);
        return userMapper.toDto(user);
    }

    public User findById(UUID uuid) {
        return userRepository.findById(uuid)
                .orElseThrow(() -> new UserNotFoundException(UserErrorEnums.USER_NOT_FOUND.getMessage()));
    }

    public void updateUser(UUID uuid, UserUpdateDto userUpdateDto) {
        User user = findById(uuid);

        User updatedUser = userMapper.partialUpdate(userUpdateDto, user);

        userRepository.save(updatedUser);
    }

    public void deleteUser(UUID uuid) {
        userRepository.deleteById(uuid);
    }
}
