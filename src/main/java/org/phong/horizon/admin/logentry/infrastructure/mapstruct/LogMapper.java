package org.phong.horizon.admin.logentry.infrastructure.mapstruct;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;
import org.phong.horizon.admin.logentry.dtos.CreateLogEntryRequest;
import org.phong.horizon.admin.logentry.dtos.LogEntryDto;
import org.phong.horizon.admin.logentry.infrastructure.entities.LogEntry;
import org.phong.horizon.user.infrastructure.mapstruct.UserMapper;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {UserMapper.class}
)
public interface LogMapper {

    @Mappings({
            @Mapping(source = "user.id", target = "userId")
    })
    LogEntryDto toDto(LogEntry logEntry);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "timestamp", ignore = true),
            @Mapping(target = "createdAt", ignore = true),
            @Mapping(target = "user", ignore = true),
    })
    LogEntry toEntity(CreateLogEntryRequest request);
}
