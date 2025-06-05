package org.phong.horizon.admin.system.maintenance.controllers;

import org.phong.horizon.admin.system.maintenance.dto.MaintenanceInfoDto;
import org.phong.horizon.admin.system.maintenance.dto.MaintenanceRequestDto;
import org.phong.horizon.admin.system.maintenance.services.MaintenanceService;
import org.phong.horizon.core.responses.RestApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/admin/system/maintenance")
public class MaintenanceController {

    private final MaintenanceService maintenanceService;

    public MaintenanceController(MaintenanceService maintenanceService) {
        this.maintenanceService = maintenanceService;
    }

    @PostMapping("/enable")
    public ResponseEntity<RestApiResponse<MaintenanceInfoDto>> enableMaintenance(@RequestBody(required = false) MaintenanceRequestDto request) {
        if (request != null) {
            maintenanceService.setMaintenanceWithDetails(true, request.getMessage(), request.getCompletionDateTime());
        } else {
            maintenanceService.setMaintenance(true);
        }
        return RestApiResponse.success(maintenanceService.getMaintenanceInfo(), "Maintenance mode enabled");
    }

    @PostMapping("/disable")
    public ResponseEntity<RestApiResponse<MaintenanceInfoDto>> disableMaintenance() {
        maintenanceService.setMaintenance(false);
        return RestApiResponse.success(maintenanceService.getMaintenanceInfo(), "Maintenance mode disabled");
    }

    @GetMapping
    public ResponseEntity<RestApiResponse<MaintenanceInfoDto>> getStatusMaintenance() {
        return RestApiResponse.success(maintenanceService.getMaintenanceInfo());
    }
}
