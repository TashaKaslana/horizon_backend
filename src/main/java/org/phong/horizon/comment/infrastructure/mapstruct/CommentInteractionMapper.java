package org.phong.horizon.comment.infrastructure.mapstruct;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.phong.horizon.comment.dtos.CommentInteractionRespond;
import org.phong.horizon.comment.infrastructure.persistence.entities.CommentInteraction;
import org.phong.horizon.user.infrastructure.mapstruct.UserMapper;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING, uses = {UserMapper.class})
public interface CommentInteractionMapper {
    CommentInteraction toEntity(CommentInteractionRespond commentInteractionRespond);

    CommentInteractionRespond toDto2(CommentInteraction commentInteraction);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    CommentInteraction partialUpdate(CommentInteractionRespond commentInteractionRespond, @MappingTarget CommentInteraction commentInteraction);
}