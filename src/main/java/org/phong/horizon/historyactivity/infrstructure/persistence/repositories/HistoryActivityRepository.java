package org.phong.horizon.historyactivity.infrstructure.persistence.repositories;

import org.phong.horizon.historyactivity.infrstructure.persistence.enitities.HistoryActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface HistoryActivityRepository extends JpaRepository<HistoryActivity, UUID>, JpaSpecificationExecutor<HistoryActivity> {
}