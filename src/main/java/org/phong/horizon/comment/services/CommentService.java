package org.phong.horizon.comment.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.phong.horizon.comment.dtos.CommentCreatedDto;
import org.phong.horizon.comment.dtos.CommentRespond;
import org.phong.horizon.comment.dtos.CreateCommentDto;
import org.phong.horizon.comment.dtos.UpdateCommentContentDto;
import org.phong.horizon.comment.enums.CommentErrorEnums;
import org.phong.horizon.comment.events.CommentCreated;
import org.phong.horizon.comment.events.CommentDeleted;
import org.phong.horizon.comment.events.CommentUpdated;
import org.phong.horizon.comment.exceptions.CommentNotFoundException;
import org.phong.horizon.comment.infrastructure.mapstruct.CommentMapper;
import org.phong.horizon.comment.infrastructure.persistence.entities.Comment;
import org.phong.horizon.comment.infrastructure.persistence.repositories.CommentRepository;
import org.phong.horizon.core.enums.Role;
import org.phong.horizon.core.services.AuthService;
import org.phong.horizon.core.utils.ObjectHelper;
import org.phong.horizon.post.infrastructure.persistence.entities.Post;
import org.phong.horizon.post.services.PostService;
import org.phong.horizon.user.infrastructure.persistence.entities.User;
import org.phong.horizon.user.services.UserService;
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
    private final UserService userService;
    private final AuthService authService;
    private final PostService postService;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public CommentCreatedDto createComment(CreateCommentDto request) {
        Comment comment = commentMapper.toEntity(request);

        Post post = postService.getRefById(request.postId());
        comment.setPost(post);

        UUID currentUserId = authService.getUserIdFromContext();
        User currentUser = userService.getRefById(currentUserId);
        comment.setUser(currentUser);

        if (request.parentCommentId() != null) {
            Comment parentComment = commentRepository.getReferenceById(request.parentCommentId());
            comment.setParentComment(parentComment);
        } else {
            comment.setParentComment(null);
        }

        Comment createdComment = commentRepository.save(comment);
        log.info("Comment created successfully, ID: {}", createdComment.getId());

        eventPublisher.publishEvent(new CommentCreated(this,
                createdComment.getId(),
                createdComment.getPost().getId(),
                createdComment.getUser().getId(),
                createdComment.getContent()
        ));

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
        Comment original = findById(commentId);
        Comment oldComment = commentMapper.cloneComment(original);

        if (isNotAllowToWrite(original)) {
            log.info("Not allow to update comment");
            throw new CommentNotFoundException(CommentErrorEnums.UNAUTHORIZED_ACCESS.getMessage());
        }

        Comment newComment = commentMapper.partialUpdate(updateCommentContentDto, original);

        commentRepository.save(newComment);
        log.info("Comment updated for commentId: {}", newComment.getId());

        eventPublisher.publishEvent(new CommentUpdated(
                this,
                newComment.getId(),
                newComment.getPost().getId(),
                newComment.getUser().getId(),
                newComment.getContent(),
                ObjectHelper.extractChangesWithCommonsLang(
                        commentMapper.toDto1(oldComment),
                        commentMapper.toDto1(newComment)
                )
        ));
    }

    @Transactional
    public void deleteCommentById(UUID commentId) {
        Comment comment = findById(commentId);
        if (isNotAllowToWrite(comment)) {
            log.info("Not allow to delete comment");
            throw new CommentNotFoundException(CommentErrorEnums.UNAUTHORIZED_ACCESS.getMessage());
        }

        commentRepository.deleteById(commentId);
        log.info("Comment deleted: {}", comment.getId());

        eventPublisher.publishEvent(new CommentDeleted(
                this,
                comment.getId(),
                comment.getPost().getId(),
                comment.getUser().getId()
        ));
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteAllComments() {
        commentRepository.deleteAll();
        log.warn("All comments deleted by admin");
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

    @Transactional(readOnly = true)
    public Comment getRefById(UUID commentId) {
        return commentRepository.getReferenceById(commentId);
    }

    @Transactional
    public void softDeleteCommentsByUserId(UUID userId) {
        commentRepository.softDeleteAllByUser_Id(userId);
    }

    @Transactional
    public void restoreCommentsByPostId(UUID postId) {
        commentRepository.restoreAllByUser_Id(postId);
    }
}
