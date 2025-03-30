package org.phong.horizon.user.controllers;

import org.phong.horizon.user.dtos.UserRespondDto;
import org.phong.horizon.user.dtos.UserUpdateDto;
import org.phong.horizon.user.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/me")
public class MeUserController {
    private final UserService userService;

    public MeUserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<UserRespondDto> getMe() {
        return ResponseEntity.ok(userService.getCurrentUser());
    }

    @PutMapping
    public ResponseEntity<Void> updateMe(@RequestBody UserUpdateDto userUpdateDto) {
        userService.updateCurrentUser(userUpdateDto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteMe() {
        userService.deleteCurrentUser();
        return ResponseEntity.noContent().build();
    }
}