package org.phong.horizon.historyactivity.infrstructure.persistence.repositories;

import org.phong.horizon.historyactivity.infrstructure.persistence.enitities.HistoryActivity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface HistoryActivityRepository extends JpaRepository<HistoryActivity, UUID>, JpaSpecificationExecutor<HistoryActivity> {
    Page<HistoryActivity> findAllByUser_Id(UUID userId, Pageable pageable);
}