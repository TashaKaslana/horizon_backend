package org.phong.horizon.admin.system.status.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO representing the status of the Redis instance.
 */
@Setter
@Getter
@NoArgsConstructor
public class RedisStatusDto {
    private String status;
    private String error;

    public RedisStatusDto(String status) {
        this.status = status;
    }

    public RedisStatusDto(String status, String error) {
        this.status = status;
        this.error = error;
    }
}
