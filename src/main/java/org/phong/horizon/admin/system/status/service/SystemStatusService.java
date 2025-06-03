package org.phong.horizon.admin.system.status.service;

import lombok.AllArgsConstructor;
import org.phong.horizon.admin.system.maintenance.services.MaintenanceService;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
@AllArgsConstructor
public class SystemStatusService {

    private final CloudinaryStatusService cloudinaryStatusService;
    private final Auth0StatusService auth0StatusService;
    private final DatabaseStatusService databaseStatusService;
    private final MaintenanceService maintenanceService;
//    private final AblyStatusService ablyStatusService;
//    private final VercelStatusService vercelStatusService;

    public Map<String, Object> getSystemStatus() {
        Map<String, Object> status = new LinkedHashMap<>();

        status.put("maintenance", maintenanceService.isMaintenance());
        status.put("database", databaseStatusService.getStatus());
        status.put("cloudinary", cloudinaryStatusService.getStatus());
        status.put("auth0", auth0StatusService.getStatus());
//        status.put("ably", ablyStatusService.getStatus());
//        status.put("vercel", vercelStatusService.getStatus());

        return status;
    }
}