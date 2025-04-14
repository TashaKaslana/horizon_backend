package org.phong.horizon.user.controllers;

import org.phong.horizon.core.enums.SystemCategory;
import org.phong.horizon.historyactivity.annotations.LogActivity;
import org.phong.horizon.historyactivity.enums.ActivityTypeCode;
import org.phong.horizon.user.dtos.UserRespondDto;
import org.phong.horizon.user.dtos.UserSummaryRespond;
import org.phong.horizon.user.services.UserService;
import org.phong.horizon.core.responses.RestApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {
    private final UserService userService;

    public AdminUserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<RestApiResponse<List<UserSummaryRespond>>> getAllUsers() {
        return RestApiResponse.success(userService.getListUserSummary());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RestApiResponse<UserRespondDto>> getUser(@PathVariable UUID id) {
        return RestApiResponse.success(userService.getUser(id));
    }

    @DeleteMapping("/{id}")
    @LogActivity(
            activityCode = ActivityTypeCode.USER_DELETE,
            description = "Admin delete a user",
            targetType = SystemCategory.USER,
            targetIdExpression = "#id"
    )
    public ResponseEntity<RestApiResponse<Void>> deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
        return RestApiResponse.noContent();
    }

    @DeleteMapping("/all")
    @LogActivity(
            activityCode = ActivityTypeCode.USER_DELETE,
            description = "Admin delete all users",
            targetType = SystemCategory.USER
    )
    public ResponseEntity<RestApiResponse<Void>> deleteAllUsers() {
        userService.deleteAllUsers();
        return RestApiResponse.noContent();
    }
}
