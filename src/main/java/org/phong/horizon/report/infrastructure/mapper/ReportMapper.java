package org.phong.horizon.report.infrastructure.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.phong.horizon.comment.infrastructure.mapstruct.CommentMapper;
import org.phong.horizon.post.infrastructure.mapstruct.PostMapper;
import org.phong.horizon.report.dto.CreateReportRequest;
import org.phong.horizon.report.dto.ReportDto;
import org.phong.horizon.report.dto.UpdateReportRequest;
import org.phong.horizon.report.infrastructure.persistence.entities.Report;
import org.phong.horizon.user.infrastructure.mapstruct.UserMapper;

@Mapper(
        componentModel = "spring",
        uses = {
                UserMapper.class,
                PostMapper.class,
                CommentMapper.class
        },
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE
)
public interface ReportMapper {
    ReportDto toDto(Report report);

    @Mapping(target = "status", constant = "PENDING")
    Report toEntity(CreateReportRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromRequest(UpdateReportRequest request, @MappingTarget Report report);
}
