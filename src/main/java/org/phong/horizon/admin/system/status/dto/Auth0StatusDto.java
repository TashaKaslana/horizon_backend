package org.phong.horizon.admin.system.status.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO representing the status of the Auth0 service.
 */
@Setter
@Getter
@NoArgsConstructor
public class Auth0StatusDto {
    private String status;
    private Long latencyMs;
    private String lastChecked;
    private String error;

    public Auth0StatusDto(String status, Long latencyMs, String lastChecked) {
        this.status = status;
        this.latencyMs = latencyMs;
        this.lastChecked = lastChecked;
    }

    public Auth0StatusDto(String status, Long latencyMs, String lastChecked, String error) {
        this.status = status;
        this.latencyMs = latencyMs;
        this.lastChecked = lastChecked;
        this.error = error;
    }

}
