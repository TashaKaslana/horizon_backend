package org.phong.horizon.post.infraustructure.mapstruct;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.phong.horizon.post.dtos.PostInteractionRespond;
import org.phong.horizon.post.infraustructure.persistence.entities.PostInteraction;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface PostInteractionMapper {
    PostInteraction toEntity(PostInteractionRespond postInteractionRespond);

    PostInteractionRespond toDto(PostInteraction postInteraction);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    PostInteraction partialUpdate(PostInteractionRespond postInteractionRespond, @MappingTarget PostInteraction postInteraction);
}