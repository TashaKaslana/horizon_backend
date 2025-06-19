package org.phong.horizon.admin.system.status.service;

import lombok.AllArgsConstructor;
import org.phong.horizon.admin.system.status.dto.AblyStatusDto;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@AllArgsConstructor
public class AblyStatusService {
    private final RestTemplate restTemplate;

    public AblyStatusDto getStatus() {
        try {
            restTemplate.getForEntity("https://rest.ably.io/time", String.class);
            return new AblyStatusDto("online");
        } catch (Exception e) {
            return new AblyStatusDto("offline", e.getMessage());
        }
    }
}
