package org.phong.horizon.admin.system.status.service;

import lombok.AllArgsConstructor;
import org.phong.horizon.admin.system.status.dto.RedisStatusDto;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class RedisStatusService {

    private final RedisConnectionFactory connectionFactory;

    public RedisStatusDto getStatus() {
        try (RedisConnection connection = connectionFactory.getConnection()) {
            String pong = connection.ping();
            if ("PONG".equalsIgnoreCase(pong)) {
                return new RedisStatusDto("online");
            }
            return new RedisStatusDto("offline", "Unexpected response: " + pong);
        } catch (Exception e) {
            return new RedisStatusDto("offline", e.getMessage());
        }
    }
}
