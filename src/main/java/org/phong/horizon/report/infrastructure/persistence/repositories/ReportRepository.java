package org.phong.horizon.report.infrastructure.persistence.repositories;

import org.phong.horizon.report.enums.ModerationItemType;
import org.phong.horizon.report.infrastructure.persistence.entities.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.time.OffsetDateTime;
import java.util.UUID;

public interface ReportRepository extends JpaRepository<Report, UUID>, JpaSpecificationExecutor<Report> {
    long countByItemType(ModerationItemType itemType);

    /**
     * Count reports that are pending review
     */
    @Query("SELECT COUNT(r) FROM Report r WHERE r.status = 'PENDING'")
    long countPendingReports();

    /**
     * Count reports created between two dates
     */
    long countByCreatedAtBetween(OffsetDateTime createdAt, OffsetDateTime createdAt2);

    /**
     * Count reports created between specified dates
     */
    default long countReportsCreatedBetween(OffsetDateTime start, OffsetDateTime end) {
        return countByCreatedAtBetween(start, end);
    }
}

