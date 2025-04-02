package org.phong.horizon.comment.services;

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
import org.phong.horizon.infrastructure.enums.Role;
import org.phong.horizon.infrastructure.services.AuthService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final AuthService authService;
    private final ApplicationEventPublisher eventPublisher;

    public CommentService(CommentRepository commentRepository,
                          CommentMapper commentMapper,
                          AuthService authService,
                          ApplicationEventPublisher eventPublisher) {
        this.commentRepository = commentRepository;
        this.commentMapper = commentMapper;
        this.authService = authService;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public CommentCreatedDto createComment(CreateCommentDto createCommentDto) {
        Comment comment = commentMapper.toEntity(createCommentDto);
        comment.setUser(authService.getUser());

        Comment createdComment = commentRepository.save(comment);

        log.info("Comment created: {}", comment);
        eventPublisher.publishEvent(new CommentCreated(createdComment));

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
