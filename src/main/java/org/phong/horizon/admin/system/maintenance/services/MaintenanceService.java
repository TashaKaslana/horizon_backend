package org.phong.horizon.admin.system.maintenance.services;

import org.phong.horizon.admin.system.maintenance.dto.MaintenanceInfoDto;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class MaintenanceService {
    private static final String DEFAULT_MESSAGE = "Service is under maintenance, please try again later.";
    private final AtomicReference<MaintenanceInfoDto> maintenanceInfo = new AtomicReference<>(
        MaintenanceInfoDto.builder()
            .active(false)
            .message(DEFAULT_MESSAGE)
            .build()
    );

    public boolean isMaintenance() {
        return maintenanceInfo.get().isActive();
    }

    public MaintenanceInfoDto getMaintenanceInfo() {
        return maintenanceInfo.get();
    }

    public void setMaintenance(boolean maintenance) {
        MaintenanceInfoDto current = maintenanceInfo.get();
        current.setActive(maintenance);

        if (maintenance && current.getActivatedAt() == null) {
            current.setActivatedAt(formatDateTime(LocalDateTime.now()));
        } else {
            current.setActivatedAt(null);
            current.setCompletionDateTime(null);
        }

        maintenanceInfo.set(current);
    }

    public void setMaintenanceWithDetails(boolean maintenance, String message, String completionDateTime) {
        MaintenanceInfoDto info = maintenanceInfo.get();
        info.setActive(maintenance);

        if (message != null && !message.isEmpty()) {
            info.setMessage(message);
        } else if (maintenance) {
            info.setMessage(DEFAULT_MESSAGE);
        }

        if (maintenance) {
            info.setActivatedAt(formatDateTime(LocalDateTime.now()));
            info.setCompletionDateTime(completionDateTime);
        } else {
            info.setActivatedAt(null);
            info.setCompletionDateTime(null);
            info.setMessage(DEFAULT_MESSAGE);
        }

        maintenanceInfo.set(info);
    }

    private String formatDateTime(LocalDateTime dateTime) {
        return dateTime.format(DateTimeFormatter.ISO_DATE_TIME);
    }
}
