package org.phong.horizon.admin.system.status.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * DTO representing the complete system status including all subsystems.
 */
@Setter
@Getter
@NoArgsConstructor
public class SystemStatusDto {
    private boolean maintenance;
    private DatabaseStatusDto database;
    private CloudinaryStatusDto cloudinary;
    private Auth0StatusDto auth0;
    private AblyStatusDto ably;
    private RedisStatusDto redis;
    // Other services can be added here as needed
    // private VercelStatusDto vercel;

    // Legacy support for Map conversion if needed
    public Map<String, Object> toMap() {
        Map<String, Object> status = new LinkedHashMap<>();
        status.put("maintenance", maintenance);
        status.put("database", database);
        status.put("cloudinary", cloudinary);
        status.put("auth0", auth0);
        status.put("ably", ably);
        status.put("redis", redis);
        return status;
    }
}
