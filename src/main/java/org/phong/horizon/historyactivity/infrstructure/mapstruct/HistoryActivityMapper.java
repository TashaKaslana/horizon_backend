package org.phong.horizon.historyactivity.infrstructure.mapstruct;

import org.mapstruct.BeanMapping;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.phong.horizon.historyactivity.dtos.CreateHistoryActivity;
import org.phong.horizon.historyactivity.dtos.HistoryActivityDto;
import org.phong.horizon.historyactivity.infrstructure.persistence.enitities.HistoryActivity;
import org.phong.horizon.user.infrastructure.mapstruct.UserMapper;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING, uses = {UserMapper.class})
public interface HistoryActivityMapper {
    @Mapping(source = "userId", target = "user.id")
    @Mapping(source = "activityTypeCode", target = "activityType.code")
    HistoryActivity toEntity(CreateHistoryActivity createHistoryActivity);

    @InheritInverseConfiguration(name = "toEntity")
    CreateHistoryActivity toDto(HistoryActivity historyActivity);

    @InheritConfiguration(name = "toEntity")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    HistoryActivity partialUpdate(CreateHistoryActivity createHistoryActivity, @MappingTarget HistoryActivity historyActivity);

    HistoryActivity toEntity(HistoryActivityDto historyActivityDto);

    HistoryActivityDto toDto1(HistoryActivity historyActivity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    HistoryActivity partialUpdate(HistoryActivityDto historyActivityDto, @MappingTarget HistoryActivity historyActivity);
}