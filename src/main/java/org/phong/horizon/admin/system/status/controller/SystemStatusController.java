package org.phong.horizon.admin.system.status.controller;

import lombok.AllArgsConstructor;
import org.phong.horizon.admin.system.status.service.SystemStatusService;
import org.phong.horizon.core.responses.RestApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/system/status")
@AllArgsConstructor
public class SystemStatusController {
    SystemStatusService systemStatusService;

    @GetMapping()
    public ResponseEntity<RestApiResponse<Map<String, Object>>> getSystemStatus() {
        return RestApiResponse.success(systemStatusService.getSystemStatus().toMap());
    }
}
