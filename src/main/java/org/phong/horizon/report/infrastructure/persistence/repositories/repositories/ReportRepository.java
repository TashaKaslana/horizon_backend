package org.phong.horizon.report.infrastructure.persistence.repositories.repositories;

import org.phong.horizon.report.enums.ModerationItemType;
import org.phong.horizon.report.infrastructure.persistence.entities.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface ReportRepository extends JpaRepository<Report, UUID>, JpaSpecificationExecutor<Report> {
    long countByItemType(ModerationItemType itemType);
}