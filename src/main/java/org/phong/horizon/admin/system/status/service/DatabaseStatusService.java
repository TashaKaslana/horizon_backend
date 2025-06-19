package org.phong.horizon.admin.system.status.service;

import jakarta.persistence.EntityManager;
import org.phong.horizon.admin.system.status.dto.DatabaseStatusDto;
import org.springframework.stereotype.Service;

@Service
public class DatabaseStatusService {

    private final EntityManager entityManager;

    public DatabaseStatusService(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public DatabaseStatusDto getStatus() {
        try {
            entityManager.createNativeQuery("SELECT 1").getSingleResult();
            return new DatabaseStatusDto("online");
        } catch (Exception e) {
            return new DatabaseStatusDto("offline", e.getMessage());
        }
    }
}
