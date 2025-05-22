package org.phong.horizon.post.subdomain.tag.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.phong.horizon.post.subdomain.tag.dto.CreateTagRequest;
import org.phong.horizon.post.subdomain.tag.dto.TagResponse;
import org.phong.horizon.post.subdomain.tag.dto.UpdateTagRequest;
import org.phong.horizon.post.subdomain.tag.entity.Tag;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface TagMapper {
    TagResponse toTagResponse(Tag tag);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    Tag toTag(CreateTagRequest createTagRequest);

    void updateTagFromRequest(UpdateTagRequest dto, @MappingTarget Tag entity);
}

