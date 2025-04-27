package org.phong.horizon.comment.infrastructure.mapstruct;

import org.mapstruct.BeanMapping;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.phong.horizon.comment.dtos.CreateReportComment;
import org.phong.horizon.comment.dtos.ReportCommentResponse;
import org.phong.horizon.comment.infrastructure.persistence.entities.ReportComment;
import org.phong.horizon.user.infrastructure.mapstruct.UserMapper;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING, uses = {CommentMapper.class, UserMapper.class})
public interface ReportCommentMapper {
    @Mapping(source = "commentId", target = "comment.id")
    ReportComment toEntity(CreateReportComment createReportComment);

    @InheritInverseConfiguration(name = "toEntity")
    CreateReportComment toDto(ReportComment reportComment);

    @InheritConfiguration(name = "toEntity")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    ReportComment partialUpdate(CreateReportComment createReportComment, @MappingTarget ReportComment reportComment);

    @Mapping(source = "user", target = "reporter")
    ReportCommentResponse toDto1(ReportComment reportComment);
}