package org.phong.horizon.post.infrastructure.mapstruct;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.phong.horizon.post.dtos.CreatePostRequest;
import org.phong.horizon.post.dtos.PostCloneDto;
import org.phong.horizon.post.dtos.PostRespond;
import org.phong.horizon.post.dtos.UpdatePostRequest;
import org.phong.horizon.post.infrastructure.persistence.entities.Post;
import org.phong.horizon.storage.infrastructure.mapper.AssetMapper;
import org.phong.horizon.storage.service.CloudinaryUrlGenerator;

@Mapper(
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        uses = {CloudinaryUrlGenerator.class, AssetMapper.class}
)
public interface PostMapper {
    Post toEntity(UpdatePostRequest updatePostRequest);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Post partialUpdate(UpdatePostRequest updatePostRequest, @MappingTarget Post post);

    Post toEntity(PostRespond postRespond);

    @Mappings({
            @Mapping(target = "videoPlaybackUrl", source = "videoAsset", qualifiedByName = "videoPlaybackUrl"),
            @Mapping(target = "videoThumbnailUrl", source = "videoAsset", qualifiedByName = "videoThumbnailUrl"),
    })
    PostRespond toDto2(Post post);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Post partialUpdate(PostRespond postRespond, @MappingTarget Post post);

    Post clonePost(Post post);

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "videoAsset.id", target = "videoAssetId")
    PostCloneDto toDto1(Post post);

    @Mapping(source = "userId", target = "user.id")
    Post toEntity(CreatePostRequest createPostRequest);

    @Mapping(source = "user.id", target = "userId")
    CreatePostRequest toDto3(Post post);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "userId", target = "user.id")
    Post partialUpdate(CreatePostRequest createPostRequest, @MappingTarget Post post);
}