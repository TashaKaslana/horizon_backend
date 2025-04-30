package org.phong.horizon.post.infrastructure.mapstruct;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.phong.horizon.post.dtos.PostReportResponse;
import org.phong.horizon.post.dtos.PostReportRequest;
import org.phong.horizon.post.infrastructure.persistence.entities.PostReport;
import org.phong.horizon.user.infrastructure.mapstruct.UserMapper;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING, uses = {UserMapper.class, PostMapper.class})
public interface PostReportMapper {
    PostReport toEntity(PostReportRequest postReportRequest);

    PostReportRequest toDto(PostReport postReport);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    PostReport partialUpdate(PostReportRequest postReportRequest, @MappingTarget PostReport postReport);

    PostReport toEntity(PostReportResponse postReportResponse);

    PostReportResponse toDto1(PostReport postReport);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    PostReport partialUpdate(PostReportResponse postReportResponse, @MappingTarget PostReport postReport);
}