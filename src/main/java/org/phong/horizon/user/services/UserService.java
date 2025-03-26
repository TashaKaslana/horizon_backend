package org.phong.horizon.user.services;

import org.phong.horizon.user.dtos.UserCreateDto;
import org.phong.horizon.user.dtos.UserRespondDto;
import org.phong.horizon.user.dtos.UserUpdateDto;
import org.phong.horizon.user.enums.UserErrorEnums;
import org.phong.horizon.user.exceptions.UserNotFoundException;
import org.phong.horizon.user.infrastructure.mapstruct.UserMapper;
import org.phong.horizon.user.infrastructure.persistent.entities.User;
import org.phong.horizon.user.infrastructure.persistent.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserService {
    private final UserMapper userMapper;
    private final UserRepository userRepository;

    public UserService(UserMapper userMapper, UserRepository userRepository) {
        this.userMapper = userMapper;
        this.userRepository = userRepository;
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
