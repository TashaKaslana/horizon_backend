package org.phong.horizon.admin.system.status.controller;

import lombok.AllArgsConstructor;
import org.phong.horizon.admin.system.status.dto.SystemStatusDto;
import org.phong.horizon.admin.system.status.service.SystemStatusService;
import org.phong.horizon.core.responses.RestApiResponse;
import org.phong.horizon.core.utils.ObjectConversion;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/admin/system/status")
@AllArgsConstructor
public class SystemStatusController {
    SystemStatusService systemStatusService;

    @GetMapping()
    public ResponseEntity<RestApiResponse<SystemStatusDto>> getSystemStatus() {
        return RestApiResponse.success(ObjectConversion.safeConvert(systemStatusService.getSystemStatus(), SystemStatusDto.class));
    }
}
