package org.phong.horizon.infrastructure.listeners;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import org.phong.horizon.infrastructure.services.AuthService;
import org.phong.horizon.infrastructure.superclass.AuditEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AuditEntityListener {
    private static AuthService authService;

    @Autowired
    public void setAuthService(AuthService authService) {
        AuditEntityListener.authService = authService;
    }

    @PrePersist
    public void prePersist(AuditEntity entity) {
        String userId = authService.getUserId();
        if (userId != null) {
            entity.setCreatedBy(UUID.fromString(userId));
        }
    }

    @PreUpdate
    public void preUpdate(AuditEntity entity) {
        String userId = authService.getUserId();
        if (userId != null) {
            entity.setUpdatedBy(UUID.fromString(userId));
        }
    }
}
