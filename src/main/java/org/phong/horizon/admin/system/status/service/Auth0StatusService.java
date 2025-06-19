package org.phong.horizon.admin.system.status.service;

import org.phong.horizon.admin.system.status.dto.Auth0StatusDto;
import org.phong.horizon.core.properties.Auth0Properties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;

@Service
public class Auth0StatusService {

    private final RestTemplate restTemplate;
    private final Auth0Properties props;

    public Auth0StatusService(RestTemplateBuilder builder, Auth0Properties props) {
        this.restTemplate = builder.build();
        this.props = props;
    }

    public Auth0StatusDto getStatus() {
        String url = props.getDomain() + "/.well-known/openid-configuration";
        long start = System.nanoTime();

        try {
            restTemplate.getForEntity(url, String.class);

            long latency = (System.nanoTime() - start) / 1_000_000;
            return new Auth0StatusDto(
                    "online",
                    latency,
                    Instant.now().toString()
            );

        } catch (Exception e) {
            long latency = (System.nanoTime() - start) / 1_000_000;
            return new Auth0StatusDto(
                    "offline",
                    latency,
                    Instant.now().toString(),
                    e.getMessage()
            );
        }
    }
}
