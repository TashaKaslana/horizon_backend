package org.phong.horizon.report.infrastructure.persistence.repositories;

import org.phong.horizon.report.enums.ModerationItemType;
import org.phong.horizon.report.enums.ModerationStatus;
import org.phong.horizon.report.infrastructure.persistence.entities.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.List;
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

    long countByCreatedAtBetweenAndStatus(OffsetDateTime createdAtAfter, OffsetDateTime createdAtBefore, ModerationStatus status);

    long countByCreatedAtBetweenAndStatusIn(OffsetDateTime createdAtAfter, OffsetDateTime createdAtBefore, Collection<ModerationStatus> statuses);

    @Query("SELECT function('date', r.createdAt), count(*) " +
            "FROM Report r " +
            "WHERE r.createdAt BETWEEN :startOfDay AND :endOfDay AND r.status = 'PENDING' " +
            "GROUP BY function('date', r.createdAt) " +
            "ORDER BY function('date', r.createdAt) asc")
    List<Object[]> countDailyPendingReports(OffsetDateTime startOfDay, OffsetDateTime endOfDay);

    @Query("SELECT function('date', r.createdAt), count(*) " +
            "FROM Report r " +
            "WHERE r.createdAt BETWEEN :startOfDay AND :endOfDay " +
            "AND r.status IN ('ACTIONTAKEN_CONTENTREMOVED', 'ACTIONTAKEN_USERWARNED', 'ACTIONTAKEN_USERBANNED', 'RESOLVED') " +
            "GROUP BY function('date', r.createdAt) " +
            "ORDER BY function('date', r.createdAt) asc")
    List<Object[]> countDailyResolvedReports(OffsetDateTime startOfDay, OffsetDateTime endOfDay);

}

