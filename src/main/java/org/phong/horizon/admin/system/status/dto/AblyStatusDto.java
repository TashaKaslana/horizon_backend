package org.phong.horizon.admin.system.status.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO representing the status of the Ably service.
 */
@Setter
@Getter
@NoArgsConstructor
public class AblyStatusDto {
    private String status;
    private String error;

    public AblyStatusDto(String status) {
        this.status = status;
    }

    public AblyStatusDto(String status, String error) {
        this.status = status;
        this.error = error;
    }
}
