package org.phong.horizon.historyactivity.infrstructure.persistence.repositories;

import org.phong.horizon.historyactivity.infrstructure.persistence.enitities.ActivityType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ActivityTypeRepository extends JpaRepository<ActivityType, Integer> {
    Optional<ActivityType> findByCode(String code);
}