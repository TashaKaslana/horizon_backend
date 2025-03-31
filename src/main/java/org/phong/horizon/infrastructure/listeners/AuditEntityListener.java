package org.phong.horizon.infrastructure.listeners;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import org.phong.horizon.infrastructure.services.AuthService;
import org.phong.horizon.infrastructure.superclass.AuditEntity;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AuditEntityListener {
    private final AuthService authService;

    public AuditEntityListener(@Lazy AuthService authService) {
        this.authService = authService;
    }

    @PrePersist
    public void prePersist(AuditEntity entity) {
        UUID userId = authService.getUserId();
        if (userId != null) {
            entity.setCreatedBy(userId);
        }
    }

    @PreUpdate
    public void preUpdate(AuditEntity entity) {
        UUID userId = authService.getUserId();
        if (userId != null) {
            entity.setUpdatedBy(userId);
        }
    }
}
