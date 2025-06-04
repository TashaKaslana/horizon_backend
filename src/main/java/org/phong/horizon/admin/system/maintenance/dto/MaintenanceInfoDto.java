package org.phong.horizon.admin.system.maintenance.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaintenanceInfoDto {
    private boolean active;
    private String message;
    private String activatedAt;
    private String completionDateTime;
}
