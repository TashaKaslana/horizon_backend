package org.phong.horizon.post.infrastructure.mapstruct;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.phong.horizon.post.dtos.PostInteractionResponse;
import org.phong.horizon.post.infrastructure.persistence.entities.PostInteraction;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface PostInteractionMapper {
    PostInteraction toEntity(PostInteractionResponse postInteractionResponse);

    PostInteractionResponse toDto(PostInteraction postInteraction);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    PostInteraction partialUpdate(PostInteractionResponse postInteractionResponse, @MappingTarget PostInteraction postInteraction);
}