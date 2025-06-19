package org.phong.horizon.admin.system.status.service;

import com.cloudinary.Cloudinary;
import lombok.AllArgsConstructor;
import org.phong.horizon.admin.system.status.dto.CloudinaryStatusDto;
import org.phong.horizon.core.utils.ObjectConversion;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;

@Service
@AllArgsConstructor
public class CloudinaryStatusService {
    private final Cloudinary cloudinary;

    public CloudinaryStatusDto getStatus() {
        long start = System.nanoTime();

        try {
            Map<?, ?> usage = cloudinary.api().usage(Map.of());
            long latency = (System.nanoTime() - start) / 1_000_000;

            return new CloudinaryStatusDto(
                    "online",
                    ObjectConversion.convertObjectToMap(usage),
                    latency,
                    Instant.now().toString()
            );
        } catch (Exception e) {
            return new CloudinaryStatusDto(
                    "offline",
                    e.getMessage()
            );
        }
    }
}
