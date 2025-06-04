package org.phong.horizon.admin.system.maintenance.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MaintenanceRequestDto {
    private String message;
    private String completionDateTime;
}
