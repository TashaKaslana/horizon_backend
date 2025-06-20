package org.phong.horizon.admin.logentry.services;

import lombok.AllArgsConstructor;
import org.phong.horizon.ably.event.AblyPublishableEvent;
import org.phong.horizon.admin.logentry.dtos.CreateLogEntryRequest;
import org.phong.horizon.admin.logentry.dtos.LogEntryDto;
import org.phong.horizon.admin.logentry.dtos.LogSearchCriteriaDto;
import org.phong.horizon.admin.logentry.dtos.BulkLogDeleteRequest;
import org.phong.horizon.admin.logentry.exceptions.LogEntryNotFoundException;
import org.phong.horizon.admin.logentry.infrastructure.entities.LogEntry;
import org.phong.horizon.admin.logentry.infrastructure.mapstruct.LogMapper;
import org.phong.horizon.admin.logentry.infrastructure.repositories.LogEntryRepository;
import org.phong.horizon.admin.logentry.infrastructure.specifications.LogSpecification;
import org.phong.horizon.admin.logentry.utils.LogEntryChannelNames;
import org.phong.horizon.user.infrastructure.persistence.entities.User;
import org.phong.horizon.user.infrastructure.persistence.repositories.UserRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

@Service
@AllArgsConstructor
public class LogService {

    private final LogEntryRepository logEntryRepository;
    private final UserRepository userRepository;
    private final LogMapper logMapper;
    private final LogSpecification logSpecification;
    private final ApplicationEventPublisher eventPublisher;

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
        LogEntryDto mapperDto = logMapper.toDto(savedLogEntry);
        eventPublisher.publishEvent(new AblyPublishableEvent() {
            @Override
            public String getChannelName() {
                return LogEntryChannelNames.adminLogs();
            }

            @Override
            public String getEventName() {
                return "admin.log.created";
            }

            @Override
            public Map<String, Object> getPayload() {
                return Map.of(
                        "logEntry", mapperDto
                );
            }
        });
        return mapperDto;
    }

    public Page<LogEntryDto> getAllLogEntries(Pageable pageable, LogSearchCriteriaDto criteria) {
        Specification<LogEntry> spec = logSpecification.filterByCriteria(criteria);

        return logEntryRepository.findAll(spec, pageable)
                .map(logMapper::toDto);
    }

    public LogEntryDto getLogEntryById(UUID id) {
        return logEntryRepository.findById(id)
                .map(logMapper::toDto)
                .orElseThrow(() -> new LogEntryNotFoundException("LogEntry not found with id: " + id));
    }

    @Transactional
    public void bulkDeleteLogEntries(BulkLogDeleteRequest request) {
        logEntryRepository.deleteAllById(request.logIds());
    }
}
