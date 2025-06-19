package org.phong.horizon.admin.system.status.service;

import lombok.AllArgsConstructor;
import org.phong.horizon.admin.system.maintenance.services.MaintenanceService;
import org.phong.horizon.admin.system.status.dto.SystemStatusDto;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class SystemStatusService {

    private final CloudinaryStatusService cloudinaryStatusService;
    private final Auth0StatusService auth0StatusService;
    private final DatabaseStatusService databaseStatusService;
    private final MaintenanceService maintenanceService;
    private final AblyStatusService ablyStatusService;
    private final RedisStatusService redisStatusService;
//    private final VercelStatusService vercelStatusService;

    @Cacheable(value = "systemStatus", condition = "false")
    public SystemStatusDto getSystemStatus() {
        SystemStatusDto status = new SystemStatusDto();

        status.setMaintenance(maintenanceService.isMaintenance());
        status.setDatabase(databaseStatusService.getStatus());
        status.setCloudinary(cloudinaryStatusService.getStatus());
        status.setAuth0(auth0StatusService.getStatus());
        status.setAbly(ablyStatusService.getStatus());
        status.setRedis(redisStatusService.getStatus());
//        status.setVercel(vercelStatusService.getStatus());

        return status;
    }
}