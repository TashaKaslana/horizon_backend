package org.phong.horizon.user.controllers;

import org.phong.horizon.historyactivity.annotations.LogActivity;
import org.phong.horizon.user.dtos.UserCreateDto;
import org.phong.horizon.user.dtos.UserCreatedDto;
import org.phong.horizon.user.dtos.UserRespondDto;
import org.phong.horizon.user.dtos.UserSummaryRespond;
import org.phong.horizon.user.dtos.UserUpdateDto;
import org.phong.horizon.user.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<UserRespondDto> getCurrentUser() {
        return ResponseEntity.ok(userService.getCurrentUser());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserSummaryRespond> getUserSummary(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.getUserSummary(id));
    }

    @PostMapping()
    @LogActivity(
            activityCode = "user_create",
            description = "Create a new user",
            targetType = "USER",
            targetIdExpression = "#result.body.id"
    )
    public ResponseEntity<UserCreatedDto> createUser(@RequestBody UserCreateDto userUpdateDto) {
        return ResponseEntity.ok(userService.createUser(userUpdateDto));
    }

    @PutMapping("/me")
    public ResponseEntity<Void> updateCurrentUser(@RequestBody UserUpdateDto userUpdateDto) {
        userService.updateCurrentUser(userUpdateDto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/me")
    @LogActivity(
            activityCode = "user_delete",
            description = "Delete current user",
            targetType = "USER",
            targetIdExpression = "#currentUserId"
    )
    public ResponseEntity<Void> deleteCurrentUser() {
        userService.deleteCurrentUser();
        return ResponseEntity.noContent().build();
    }
}
