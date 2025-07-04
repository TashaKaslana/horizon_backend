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
import org.phong.horizon.post.dtos.PostAdminViewDto;
import org.phong.horizon.post.dtos.PostCloneDto;
import org.phong.horizon.post.dtos.PostCreatedDto;
import org.phong.horizon.post.dtos.PostResponse;
import org.phong.horizon.post.dtos.PostSummaryResponse;
import org.phong.horizon.post.dtos.UpdatePostRequest;
import org.phong.horizon.post.infrastructure.persistence.entities.Post;
import org.phong.horizon.post.utils.PostUtils;
import org.phong.horizon.storage.infrastructure.mapper.AssetMapper;
import org.phong.horizon.storage.service.CloudinaryUrlGenerator;

@Mapper(
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        uses = {CloudinaryUrlGenerator.class, AssetMapper.class, PostUtils.class,
                org.phong.horizon.post.subdomain.tag.service.PostTagService.class}
)
public interface PostMapper {
    Post toEntity(UpdatePostRequest updatePostRequest);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Post partialUpdate(UpdatePostRequest updatePostRequest, @MappingTarget Post post);

    @Mapping(source = "categoryName", target = "category.name")
    Post toEntity(PostResponse postResponse);

    @Mapping(source = "category.name", target = "categoryName")
    @Mappings({
            @Mapping(target = "videoPlaybackUrl", source = "videoAsset", qualifiedByName = "videoPlaybackUrl"),
            @Mapping(target = "videoThumbnailUrl", source = "videoAsset", qualifiedByName = "videoThumbnailUrl"),
            @Mapping(target = "tags", source = "id", qualifiedByName = "getTagNames")
    })
    PostResponse toDto2(Post post);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Post partialUpdate(PostResponse postResponse, @MappingTarget Post post);

    Post clonePost(Post post);

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "videoAsset.id", target = "videoAssetId")
    @Mapping(target = "tags", source = "id", qualifiedByName = "getTagNames")
    PostCloneDto toDto1(Post post);

    @Mapping(source = "categoryName", target = "category.name")
    Post toEntity(CreatePostRequest createPostRequest);

    @Mapping(source = "category.name", target = "categoryName")
    CreatePostRequest toDto3(Post post);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Post partialUpdate(CreatePostRequest createPostRequest, @MappingTarget Post post);

    @Mappings({
            @Mapping(target = "videoThumbnailUrl", source = "videoAsset", qualifiedByName = "videoThumbnailUrl"),
//            @Mapping(target = "tags", source = "id", qualifiedByName = "getTagNames")
    })
    @Mapping(source = "user.id", target = "userId")
    PostSummaryResponse toDto(Post post);

    PostCreatedDto postToPostCreatedDto(Post post);

    Post toEntity(PostSummaryResponse postSummaryResponse);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Post partialUpdate(PostSummaryResponse postSummaryResponse, @MappingTarget Post post);

    @Mappings({
            @Mapping(target = "totalViews", source = "id", qualifiedByName = "postViewCount"),
            @Mapping(target = "totalInteractions", source = "id", qualifiedByName = "postInteractionCount"),
            @Mapping(target = "videoPlaybackUrl", source = "videoAsset", qualifiedByName = "videoPlaybackUrl"),
            @Mapping(target = "videoThumbnailUrl", source = "videoAsset", qualifiedByName = "videoThumbnailUrl"),
            @Mapping(target = "tags", source = "id", qualifiedByName = "getTagNames")
    })
    PostAdminViewDto toAdminViewDto(Post post);
}
