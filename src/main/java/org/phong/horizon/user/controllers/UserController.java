package org.phong.horizon.user.controllers;

import jakarta.validation.Valid;
import org.phong.horizon.core.enums.SystemCategory;
import org.phong.horizon.core.responses.RestApiResponse;
import org.phong.horizon.historyactivity.annotations.LogActivity;
import org.phong.horizon.historyactivity.enums.ActivityTypeCode;
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
    public ResponseEntity<RestApiResponse<UserRespondDto>> getCurrentUser() {
        return RestApiResponse.success(userService.getCurrentUser());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RestApiResponse<UserSummaryRespond>> getUserSummary(@PathVariable UUID id) {
        return RestApiResponse.success(userService.getUserSummary(id));
    }

    @PostMapping()
    @LogActivity(
            activityCode = ActivityTypeCode.USER_CREATE,
            description = "Create a new user",
            targetType = SystemCategory.USER,
            targetIdExpression = "#result.body.data.id"
    )
    public ResponseEntity<RestApiResponse<UserCreatedDto>> createUser(@Valid @RequestBody UserCreateDto userUpdateDto) {
        return RestApiResponse.created(userService.createUser(userUpdateDto));
    }

    @PutMapping("/me")
    public ResponseEntity<RestApiResponse<Void>> updateCurrentUser(@Valid @RequestBody UserUpdateDto userUpdateDto) {
        userService.updateCurrentUser(userUpdateDto);
        return RestApiResponse.noContent();
    }

    @DeleteMapping("/me")
    @LogActivity(
            activityCode = ActivityTypeCode.USER_DELETE,
            description = "Delete current user",
            targetType = SystemCategory.USER,
            targetIdExpression = "#currentUserId"
    )
    public ResponseEntity<RestApiResponse<Void>> deleteCurrentUser() {
        userService.deleteCurrentUser();
        return RestApiResponse.noContent();
    }

    @PutMapping("/{id}/restore")
    @LogActivity(
            activityCode = ActivityTypeCode.USER_RESTORE,
            description = "Restore user",
            targetType = SystemCategory.USER,
            targetIdExpression = "#id"
    )
    public ResponseEntity<RestApiResponse<Void>> restoreUser(@PathVariable UUID id) {
        userService.restoreUserById(id);
        return RestApiResponse.noContent();
    }
}
