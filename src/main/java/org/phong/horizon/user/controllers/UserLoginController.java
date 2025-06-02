package org.phong.horizon.user.controllers;

import lombok.RequiredArgsConstructor;
import org.phong.horizon.core.responses.RestApiResponse;
import org.phong.horizon.core.services.AuthService;
import org.phong.horizon.user.dtos.UserLoginStatusDto;
import org.phong.horizon.user.dtos.UserRespondDto;
import org.phong.horizon.user.infrastructure.persistence.entities.User;
import org.phong.horizon.user.infrastructure.persistence.repositories.UserRepository;
import org.phong.horizon.user.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("/api/users/login")
@RequiredArgsConstructor
public class UserLoginController {
    private final UserRepository userRepository;
    private final UserService userService;
    private final AuthService authService;

    /**
     * Update current user's login status
     * @param request The login status update request
     * @return The updated user information
     */
    @PutMapping("/status")
    public ResponseEntity<RestApiResponse<UserRespondDto>> updateLoginStatus(@RequestBody UserLoginStatusDto request) {
        User user = authService.getUser();
        user.setIsLogin(request.getIsLogin());

        if (Boolean.TRUE.equals(request.getIsLogin())) {
            // Only update the last login time when logging in, not when logging out
            user.setLastLogin(Instant.now());
        }

        User savedUser = userRepository.save(user);
        return RestApiResponse.success(userService.getUser(savedUser.getId()));
    }

    /**
     * Admin endpoint to update a specific user's login status
     * @param userId The ID of the user to update
     * @param request The login status update request
     * @return The updated user information
     */
    @PutMapping("/{userId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RestApiResponse<UserRespondDto>> updateUserLoginStatus(
            @PathVariable UUID userId,
            @RequestBody UserLoginStatusDto request) {
        User user = userService.findById(userId);
        user.setIsLogin(request.getIsLogin());

        if (Boolean.TRUE.equals(request.getIsLogin())) {
            // Only update the last login time when logging in, not when logging out
            user.setLastLogin(Instant.now());
        }

        User savedUser = userRepository.save(user);
        return RestApiResponse.success(userService.getUser(savedUser.getId()));
    }

    /**
     * Get current user's login status
     * @return The login status information
     */
    @GetMapping("/status")
    public ResponseEntity<RestApiResponse<UserLoginStatusDto>> getLoginStatus() {
        User user = authService.getUser();
        UserLoginStatusDto status = new UserLoginStatusDto(
            user.getIsLogin(),
            user.getLastLogin()
        );
        return RestApiResponse.success(status);
    }

    /**
     * Admin endpoint to get a specific user's login status
     * @param userId The ID of the user to check
     * @return The login status information
     */
    @GetMapping("/{userId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RestApiResponse<UserLoginStatusDto>> getUserLoginStatus(@PathVariable UUID userId) {
        User user = userService.findById(userId);
        UserLoginStatusDto status = new UserLoginStatusDto(
            user.getIsLogin(),
            user.getLastLogin()
        );
        return RestApiResponse.success(status);
    }
}
