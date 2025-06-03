package org.phong.horizon.admin.system.status.service;

import com.cloudinary.Cloudinary;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;

@Service
@AllArgsConstructor
public class CloudinaryStatusService {
    private final Cloudinary cloudinary;

    @Cacheable("cloudinary-usage")
    public Map<String, Object> getStatus() {
        long start = System.nanoTime();

        try {
            Map<?, ?> usage = cloudinary.api().usage(Map.of());
            long latency = (System.nanoTime() - start) / 1_000_000;

            return Map.of(
                    "status", "online",
                    "storage", usage,
                    "latency_ms", latency,
                    "last_checked", Instant.now().toString()
            );
        } catch (Exception e) {
            return Map.of(
                    "status", "offline",
                    "error", e.getMessage()
            );
        }
    }
}

