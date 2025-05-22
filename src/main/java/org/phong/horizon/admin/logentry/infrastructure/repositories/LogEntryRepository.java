package org.phong.horizon.admin.logentry.infrastructure.repositories;

import org.phong.horizon.admin.logentry.infrastructure.entities.LogEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LogEntryRepository extends JpaRepository<LogEntry, UUID> {
}