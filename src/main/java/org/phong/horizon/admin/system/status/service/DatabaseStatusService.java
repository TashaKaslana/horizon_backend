package org.phong.horizon.admin.system.status.service;

import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class DatabaseStatusService {

    private final EntityManager entityManager;

    public DatabaseStatusService(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public Map<String, Object> getStatus() {
        try {
            entityManager.createNativeQuery("SELECT 1").getSingleResult();
            return Map.of("status", "online");
        } catch (Exception e) {
            return Map.of("status", "offline", "error", e.getMessage());
        }
    }
}
