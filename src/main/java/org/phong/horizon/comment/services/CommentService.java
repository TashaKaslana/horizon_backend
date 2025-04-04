package org.phong.horizon.comment.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.phong.horizon.comment.dtos.CommentCreatedDto;
import org.phong.horizon.comment.dtos.CommentRespond;
import org.phong.horizon.comment.dtos.CreateCommentDto;
import org.phong.horizon.comment.dtos.UpdateCommentContentDto;
import org.phong.horizon.comment.enums.CommentErrorEnums;
import org.phong.horizon.comment.events.CommentDeleted;
import org.phong.horizon.comment.events.CommentUpdated;
import org.phong.horizon.comment.exceptions.CommentNotFoundException;
import org.phong.horizon.comment.infrastructure.mapstruct.CommentMapper;
import org.phong.horizon.comment.infrastructure.persistence.entities.Comment;
import org.phong.horizon.comment.infrastructure.persistence.repositories.CommentRepository;
import org.phong.horizon.infrastructure.enums.Role;
import org.phong.horizon.infrastructure.services.AuthService;
import org.phong.horizon.post.infraustructure.persistence.entities.Post;
import org.phong.horizon.post.services.PostService;
import org.phong.horizon.user.infrastructure.persistence.entities.User;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final AuthService authService;
    private final PostService postService;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public CommentCreatedDto createComment(CreateCommentDto request) {
        Comment comment = commentMapper.toEntity(request);

        Post post = postService.findPostById(request.postId());
        comment.setPost(post);

        User user = authService.getUser();
        comment.setUser(user);

        if (request.parentCommentId() != null) {
            Comment parentComment = commentRepository.findById(request.parentCommentId())
                    .orElseThrow(() -> new CommentNotFoundException(CommentErrorEnums.COMMENT_NOT_FOUND.getMessage()));
            comment.setParentComment(parentComment);
        } else {
            comment.setParentComment(null);
        }

        Comment createdComment = commentRepository.save(comment);
        log.info("Comment created successfully, ID: {}", createdComment.getId());

        eventPublisher.publishEvent(commentMapper.toDto3(createdComment));

        return new CommentCreatedDto(createdComment.getId());
    }

    @Transactional(readOnly = true)
    public CommentRespond getCommentById(UUID commentId) {
        Comment comment = findById(commentId);

        return commentMapper.toDto(comment);
    }

    @Transactional(readOnly = true)
    public List<CommentRespond> getAllCommentsByPostId(UUID postId) {
        List<Comment> comments = commentRepository.findAllByPost_Id(postId);

        return comments.stream().map(commentMapper::toDto).collect(Collectors.toList());
    }

    @Transactional
    public void updateCommentContent(UUID commentId, UpdateCommentContentDto updateCommentContentDto) {
        Comment comment = findById(commentId);

        if (isNotAllowToWrite(comment)) {
            log.info("Not allow to update comment");
            throw new CommentNotFoundException(CommentErrorEnums.UNAUTHORIZED_ACCESS.getMessage());
        }

        Comment newComment = commentMapper.partialUpdate(updateCommentContentDto, comment);

        eventPublisher.publishEvent(new CommentUpdated(newComment));

        commentRepository.save(newComment);
    }

    @Transactional
    public void deleteCommentById(UUID commentId) {
        Comment comment = findById(commentId);
        if (isNotAllowToWrite(comment)) {
            log.info("Not allow to delete comment");
            throw new CommentNotFoundException(CommentErrorEnums.UNAUTHORIZED_ACCESS.getMessage());
        }

        log.info("Comment deleted: {}", comment);
        eventPublisher.publishEvent(new CommentDeleted(comment));

        commentRepository.deleteById(commentId);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteAllComments() {
        commentRepository.deleteAll();
    }

    protected boolean isNotAllowToWrite(Comment comment) {
        return !authService.isPrincipal(comment.getUser().getId()) && !authService.hasRole(Role.ADMIN);
    }

    @Transactional(readOnly = true)
    public Comment findById(UUID commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> {
                    log.info("Comment not found: {}", commentId);
                    return new CommentNotFoundException(CommentErrorEnums.COMMENT_NOT_FOUND.getMessage());
                });
    }
}
