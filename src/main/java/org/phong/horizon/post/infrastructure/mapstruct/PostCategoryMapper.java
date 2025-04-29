package org.phong.horizon.post.infrastructure.mapstruct;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.phong.horizon.post.dtos.PostCategorySummary;
import org.phong.horizon.post.dtos.UpdatePostCategoryRequest;
import org.phong.horizon.post.infrastructure.persistence.entities.PostCategory;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface PostCategoryMapper {
    PostCategory toEntity(PostCategorySummary postCategorySummary);

    PostCategorySummary toDto(PostCategory postCategory);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    PostCategory partialUpdate(PostCategorySummary postCategorySummary, @MappingTarget PostCategory postCategory);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    PostCategory partialUpdate(UpdatePostCategoryRequest updatePostCategoryRequest, @MappingTarget PostCategory postCategory);

    PostCategory toClone(PostCategory postCategory);
}