package org.phong.horizon.settings.infrastructure.persistence.repositories;

import org.phong.horizon.settings.infrastructure.persistence.entities.UserSetting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserSettingRepository extends JpaRepository<UserSetting, UUID> {
    Optional<UserSetting> findByUser_Id(UUID userId);
    boolean existsByUser_Id(UUID userId);
}
