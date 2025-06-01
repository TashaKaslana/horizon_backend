package org.phong.horizon.admin.logentry.infrastructure.repositories;

import org.phong.horizon.admin.logentry.infrastructure.entities.LogEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface LogEntryRepository extends JpaRepository<LogEntry, UUID>, JpaSpecificationExecutor<LogEntry> {
    @Query("SELECT CAST(l.timestamp AS LocalDate) as logDate, COUNT(l) as count " +
           "FROM LogEntry l " +
           "WHERE l.timestamp >= :startDate AND l.timestamp < :endDate " +
           "AND l.severity IN ('ERROR', 'CRITICAL') " +
           "GROUP BY CAST(l.timestamp AS LocalDate) " +
           "ORDER BY CAST(l.timestamp AS LocalDate) ASC")
    List<Object[]> findDailyErrorAndCriticalCounts(
            @Param("startDate") OffsetDateTime startDate,
            @Param("endDate") OffsetDateTime endDate);
}
