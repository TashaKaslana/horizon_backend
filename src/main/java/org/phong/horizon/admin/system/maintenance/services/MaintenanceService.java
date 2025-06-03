package org.phong.horizon.admin.system.maintenance.services;

import org.springframework.stereotype.Service;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class MaintenanceService {
    private final AtomicBoolean maintenanceMode = new AtomicBoolean(false);

    public boolean isMaintenance() {
        return maintenanceMode.get();
    }

    public void setMaintenance(boolean maintenance) {
        maintenanceMode.set(maintenance);
    }
}
