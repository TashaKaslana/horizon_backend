package org.phong.horizon.post.subdomain.category.mapstruct;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.phong.horizon.post.subdomain.category.dtos.PostCategorySummary;
import org.phong.horizon.post.subdomain.category.dtos.UpdatePostCategoryRequest;
import org.phong.horizon.post.subdomain.category.entities.PostCategory;

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