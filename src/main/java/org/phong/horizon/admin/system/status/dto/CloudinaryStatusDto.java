package org.phong.horizon.admin.system.status.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

/**
 * DTO representing the status of the Cloudinary service.
 */
@Setter
@Getter
@NoArgsConstructor
public class CloudinaryStatusDto {
    private String status;
    private Map<String, Object> storage;
    private Long latencyMs;
    private String lastChecked;
    private String error;

    public CloudinaryStatusDto(String status, Map<String, Object> storage, Long latencyMs, String lastChecked) {
        this.status = status;
        this.storage = storage;
        this.latencyMs = latencyMs;
        this.lastChecked = lastChecked;
    }

    public CloudinaryStatusDto(String status, String error) {
        this.status = status;
        this.error = error;
    }

}
