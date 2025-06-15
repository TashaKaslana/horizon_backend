package org.phong.horizon.settings.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.phong.horizon.core.responses.RestApiResponse;
import org.phong.horizon.settings.dtos.UserSettingDto;
import org.phong.horizon.settings.services.UserSettingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/settings")
@RequiredArgsConstructor
public class UserSettingController {
    private final UserSettingService service;

    @GetMapping("/me")
    public ResponseEntity<RestApiResponse<UserSettingDto>> getMySetting() {
        return RestApiResponse.success(service.getMySetting());
    }

    @PutMapping("/me")
    public ResponseEntity<RestApiResponse<UserSettingDto>> updateMySetting(@Valid @RequestBody UserSettingDto dto) {
        return RestApiResponse.success(service.updateMySetting(dto));
    }
}
