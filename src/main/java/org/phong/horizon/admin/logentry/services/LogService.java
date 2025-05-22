package org.phong.horizon.admin.logentry.services;

import lombok.AllArgsConstructor;
import org.phong.horizon.admin.logentry.dtos.CreateLogEntryRequest;
import org.phong.horizon.admin.logentry.dtos.LogEntryDto;
import org.phong.horizon.admin.logentry.infrastructure.entities.LogEntry;
import org.phong.horizon.admin.logentry.infrastructure.mapstruct.LogMapper;
import org.phong.horizon.admin.logentry.infrastructure.repositories.LogEntryRepository;
import org.phong.horizon.user.infrastructure.persistence.entities.User;
import org.phong.horizon.user.infrastructure.persistence.repositories.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@AllArgsConstructor
public class LogService {

    private final LogEntryRepository logEntryRepository;
    private final UserRepository userRepository;
    private final LogMapper logMapper;

    @Transactional
    public LogEntryDto createLogEntry(CreateLogEntryRequest request) {
        LogEntry logEntry = logMapper.toEntity(request);
        logEntry.setTimestamp(OffsetDateTime.now());

        if (request.getUserId() != null) {
            User user = userRepository.findById(request.getUserId())
                    .orElse(null);
            logEntry.setUser(user);
        }

        LogEntry savedLogEntry = logEntryRepository.save(logEntry);
        return logMapper.toDto(savedLogEntry);
    }

    public Page<LogEntryDto> getAllLogEntries(Pageable pageable) {
        return logEntryRepository.findAll(pageable)
                .map(logMapper::toDto);
    }

    public LogEntryDto getLogEntryById(UUID id) {
        return logEntryRepository.findById(id)
                .map(logMapper::toDto)
                .orElseThrow(() -> new RuntimeException("LogEntry not found with id: " + id));
    }

    // You can add more methods here for searching/filtering logs, e.g.:
    // - findBySeverity(LogSeverity severity, Pageable pageable)
    // - findBySource(String source, Pageable pageable)
    // - findByUserId(UUID userId, Pageable pageable)
    // - findByTimestampBetween(OffsetDateTime start, OffsetDateTime end, Pageable pageable)
}

