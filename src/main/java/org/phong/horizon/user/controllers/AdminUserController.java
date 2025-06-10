package org.phong.horizon.user.controllers;

import org.phong.horizon.core.enums.SystemCategory;
import org.phong.horizon.historyactivity.annotations.LogActivity;
import org.phong.horizon.historyactivity.enums.ActivityTypeCode;
import org.phong.horizon.user.dtos.BulkUserDeleteRequest;
import org.phong.horizon.user.dtos.UserAccountUpdate;
import org.phong.horizon.user.dtos.UserIntroduction;
import org.phong.horizon.user.dtos.UserRespondDto;
import org.phong.horizon.user.dtos.UserSummaryRespond;
import org.phong.horizon.user.dtos.UserUpdateInfoDto;
import org.phong.horizon.user.services.UserService;
import org.phong.horizon.core.responses.RestApiResponse;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
    public ResponseEntity<RestApiResponse<List<UserSummaryRespond>>> getAllUsers(@ParameterObject Pageable pageable) {
        return RestApiResponse.success(userService.getListUserSummary(pageable));
    }

    @GetMapping("/intro/all")
    public ResponseEntity<RestApiResponse<List<UserIntroduction>>> getAllUserIntroductions(@ParameterObject Pageable pageable) {
        return RestApiResponse.success(userService.getUserIntroductionList(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RestApiResponse<UserRespondDto>> getUser(@PathVariable UUID id) {
        return RestApiResponse.success(userService.getUser(id));
    }

    @PutMapping("/{id}/account")
    public ResponseEntity<RestApiResponse<UserRespondDto>> updateUserAccount(@PathVariable UUID id, @RequestBody UserAccountUpdate request) {
        return RestApiResponse.success(userService.updateUserAccount(request, id));
    }

    @PutMapping("/{id}/info")
    public ResponseEntity<RestApiResponse<UserRespondDto>> updateUserInfo(@PathVariable UUID id, @RequestBody UserUpdateInfoDto request) {
        return RestApiResponse.success(userService.updateUserInfo(id, request));
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

    @DeleteMapping("/bulk")
    @LogActivity(
            activityCode = ActivityTypeCode.USER_DELETE,
            description = "Admin bulk delete users",
            targetType = SystemCategory.USER
    )
    public ResponseEntity<RestApiResponse<Void>> bulkDeleteUsers(@RequestBody BulkUserDeleteRequest request) {
        userService.bulkDeleteUsers(request);
        return RestApiResponse.noContent();
    }
}
