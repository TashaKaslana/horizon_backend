package org.phong.horizon.user.controllers;

import jakarta.validation.Valid;
import org.phong.horizon.core.enums.SystemCategory;
import org.phong.horizon.core.responses.RestApiResponse;
import org.phong.horizon.historyactivity.annotations.LogActivity;
import org.phong.horizon.historyactivity.enums.ActivityTypeCode;
import org.phong.horizon.user.dtos.UserAccountUpdate;
import org.phong.horizon.user.dtos.UserCreateDto;
import org.phong.horizon.user.dtos.UserCreatedDto;
import org.phong.horizon.user.dtos.UserImageUpdate;
import org.phong.horizon.user.dtos.UserIntroduction;
import org.phong.horizon.user.dtos.UserRespondDto;
import org.phong.horizon.user.dtos.UserSummaryRespond;
import org.phong.horizon.user.dtos.UserUpdateInfoDto;
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

    @GetMapping("/{id}/intro")
    public ResponseEntity<RestApiResponse<UserIntroduction>> getUserIntroduction(@PathVariable UUID id) {
        return RestApiResponse.success(userService.getUserIntroduction(id));
    }

    @GetMapping("/{userId}/exists")
    public ResponseEntity<RestApiResponse<Boolean>> isUserExists(@PathVariable UUID userId) {
        return RestApiResponse.success(userService.existsById(userId));
    }

    @GetMapping("/auth0/{auth0Id}/exists")
    public ResponseEntity<RestApiResponse<Boolean>> isUserExistsByAuth0Id(@PathVariable String auth0Id) {
        return RestApiResponse.success(userService.isUserExistsWithAuth0Id(auth0Id));
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

    @PutMapping("/me/info")
    public ResponseEntity<RestApiResponse<UserRespondDto>> updateCurrentUserInfo(@Valid @RequestBody UserUpdateInfoDto userUpdateInfoDto) {
        return RestApiResponse.success(userService.updateCurrentUserInfo(userUpdateInfoDto));
    }

    @PutMapping("/me/account")
    public ResponseEntity<RestApiResponse<UserRespondDto>> updateUserAccount(@Valid @RequestBody UserAccountUpdate request) {
        return RestApiResponse.success(userService.updateMeAccount(request));
    }

    @PutMapping("me/update-image")
    public ResponseEntity<RestApiResponse<Void>> updateUserAccount(@Valid @RequestBody UserImageUpdate request) {
        userService.updateCurrentUserImage(request);
        return RestApiResponse.success();
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
