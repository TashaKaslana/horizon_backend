package org.phong.horizon.admin.system.status.service;

import org.phong.horizon.core.properties.Auth0Properties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.Map;

@Service
public class Auth0StatusService {

    private final RestTemplate restTemplate;
    private final Auth0Properties props;

    public Auth0StatusService(RestTemplateBuilder builder, Auth0Properties props) {
        this.restTemplate = builder.build();
        this.props = props;
    }

    @Cacheable("auth0-status")
    public Map<String, Object> getStatus() {
        String url = props.getDomain() + "/.well-known/openid-configuration";
        long start = System.nanoTime();

        try {
            restTemplate.getForEntity(url, String.class);

            long latency = (System.nanoTime() - start) / 1_000_000;
            return Map.of(
                    "status", "online",
                    "latency_ms", latency,
                    "last_checked", Instant.now().toString()
            );

        } catch (Exception e) {
            long latency = (System.nanoTime() - start) / 1_000_000;
            return Map.of(
                    "status", "offline",
                    "latency_ms", latency,
                    "last_checked", Instant.now().toString(),
                    "error", e.getMessage()
            );
        }
    }
}

