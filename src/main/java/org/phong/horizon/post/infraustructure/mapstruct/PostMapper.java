package org.phong.horizon.post.infraustructure.mapstruct;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.phong.horizon.post.dtos.CreatePostRequest;
import org.phong.horizon.post.dtos.GetPostRequest;
import org.phong.horizon.post.dtos.PostRespond;
import org.phong.horizon.post.dtos.UpdatePostRequest;
import org.phong.horizon.post.infraustructure.persistence.entities.Post;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface PostMapper {
    Post toEntity(CreatePostRequest createPostRequest);

    CreatePostRequest toDto(Post post);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Post partialUpdate(CreatePostRequest createPostRequest, @MappingTarget Post post);

    Post toEntity(UpdatePostRequest updatePostRequest);

    UpdatePostRequest toDto1(Post post);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Post partialUpdate(UpdatePostRequest updatePostRequest, @MappingTarget Post post);

    Post toEntity(PostRespond postRespond);

    PostRespond toDto2(Post post);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Post partialUpdate(PostRespond postRespond, @MappingTarget Post post);

    @Mapping(source = "userId", target = "user.id")
    Post toEntity(GetPostRequest getPostRequest);

    @Mapping(source = "user.id", target = "userId")
    GetPostRequest toDto3(Post post);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "userId", target = "user.id")
    Post partialUpdate(GetPostRequest getPostRequest, @MappingTarget Post post);
}