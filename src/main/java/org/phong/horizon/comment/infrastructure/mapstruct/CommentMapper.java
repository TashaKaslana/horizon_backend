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
import org.phong.horizon.comment.dtos.CommentCloneDto;
import org.phong.horizon.comment.dtos.CommentResponseWithPostDetails;
import org.phong.horizon.comment.dtos.CommentRespond;
import org.phong.horizon.comment.dtos.CreateCommentDto;
import org.phong.horizon.comment.dtos.UpdateCommentContentDto;
import org.phong.horizon.comment.infrastructure.persistence.entities.Comment;
import org.phong.horizon.post.infrastructure.mapstruct.PostMapper;
import org.phong.horizon.user.infrastructure.mapstruct.UserMapper;
import org.phong.horizon.comment.utils.CommentUtils;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING,
        uses = {PostMapper.class, UserMapper.class, CommentUtils.class})
public interface CommentMapper {
    @Mapping(source = "postId", target = "post.id")
    @Mapping(source = "parentCommentId", target = "parentComment.id")
    Comment toEntity(CommentRespond commentRespond);

    @Mapping(source = "post.id", target = "postId")
    @Mapping(source = "parentComment.id", target = "parentCommentId")
    @Mapping(target = "interactionCount", source = "id", qualifiedByName = "commentInteractionCount")
    CommentRespond toDto(Comment comment);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "parentCommentId", target = "parentComment.id")
    Comment partialUpdate(CommentRespond commentRespond, @MappingTarget Comment comment);

    @Mapping(source = "parentCommentId", target = "parentComment.id")
    @Mapping(source = "postId", target = "post.id")
    Comment toEntity(CreateCommentDto createCommentDto);

    @InheritConfiguration(name = "toEntity")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Comment partialUpdate(CreateCommentDto createCommentDto, @MappingTarget Comment comment);

    Comment toEntity(UpdateCommentContentDto updateCommentContentDto);

    UpdateCommentContentDto toDto2(Comment comment);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Comment partialUpdate(UpdateCommentContentDto updateCommentContentDto, @MappingTarget Comment comment);

    @Mapping(source = "parentComment.id", target = "parentCommentId")
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "post.id", target = "postId")
    CommentCloneDto toDto1(Comment comment);

    Comment cloneComment(Comment comment);

    @Mapping(source = "parentCommentContent", target = "parentComment.content")
    @Mapping(source = "parentCommentId", target = "parentComment.id")
    Comment toEntity(CommentResponseWithPostDetails commentResponseWithPostDetails);

    @InheritInverseConfiguration(name = "toEntity")
    @Mapping(target = "interactionCount", source = "id", qualifiedByName = "commentInteractionCount")
    CommentResponseWithPostDetails toDto5(Comment comment);

    @InheritConfiguration(name = "toEntity")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Comment partialUpdate(CommentResponseWithPostDetails commentResponseWithPostDetails, @MappingTarget Comment comment);
}
