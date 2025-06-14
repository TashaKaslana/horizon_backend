package org.phong.horizon.comment.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.phong.horizon.comment.dtos.BulkCommentDeleteRequest;
import org.phong.horizon.comment.dtos.BulkCommentUpdateRequest;
import org.phong.horizon.comment.dtos.CommentRespond;
import org.phong.horizon.comment.dtos.CommentResponseWithPostDetails;
import org.phong.horizon.comment.dtos.CreateCommentDto;
import org.phong.horizon.comment.dtos.UpdateCommentContentDto;
import org.phong.horizon.comment.enums.CommentErrorEnums;
import org.phong.horizon.comment.enums.CommentStatus;
import org.phong.horizon.comment.events.CommentCreated;
import org.phong.horizon.comment.events.CommentDeleted;
import org.phong.horizon.comment.events.CommentPinned;
import org.phong.horizon.comment.events.CommentUnPinned;
import org.phong.horizon.comment.events.CommentUpdated;
import org.phong.horizon.comment.events.BulkCommentsDeletedEvent;
import org.phong.horizon.comment.events.BulkCommentsUpdatedEvent;
import org.phong.horizon.comment.exceptions.CommentNotFoundException;
import org.phong.horizon.comment.infrastructure.mapstruct.CommentMapper;
import org.phong.horizon.comment.infrastructure.persistence.entities.Comment;
import org.phong.horizon.comment.infrastructure.persistence.repositories.CommentRepository;
import org.phong.horizon.core.enums.Role;
import org.phong.horizon.core.services.AuthService;
import org.phong.horizon.core.utils.HttpRequestUtils;
import org.phong.horizon.core.utils.ObjectHelper;
import org.phong.horizon.post.infrastructure.persistence.entities.Post;
import org.phong.horizon.post.services.PostService;
import org.phong.horizon.user.infrastructure.mapstruct.UserMapper;
import org.phong.horizon.user.infrastructure.persistence.entities.User;
import org.phong.horizon.user.services.UserService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class CommentService {
    private final UserMapper userMapper;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final UserService userService;
    private final AuthService authService;
    private final PostService postService;
    private final ApplicationEventPublisher eventPublisher;
    
    @Transactional
    public CommentRespond createComment(CreateCommentDto request) {
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

        comment.setStatus(CommentStatus.PENDING);

        Comment createdComment = commentRepository.save(comment);
        log.info("Comment created successfully, ID: {}", createdComment.getId());

        CommentRespond createdCommentDto = commentMapper.toDto(createdComment);

        eventPublisher.publishEvent(new CommentCreated(this,
                createdComment.getId(),
                createdComment.getPost().getId(),
                userMapper.toDto6(createdComment.getUser()),
                createdComment.getContent(),
                createdComment.getParentComment() != null ? createdComment.getParentComment().getId() : null,
                currentUserId,
                createdComment.getCreatedAt()
        ));

        return createdCommentDto;
    }

    @Transactional(readOnly = true)
    public CommentRespond getCommentById(UUID commentId) {
        Comment comment = findById(commentId);

        return commentMapper.toDto(comment);
    }

    @Transactional(readOnly = true)
    public Page<CommentRespond> getAllCommentsByPostId(Pageable pageable, UUID postId) {
        Page<Comment> comments = commentRepository.findAllByPost_Id(pageable, postId);

        return comments.map(commentMapper::toDto);
    }

    @Transactional
    public Page<CommentResponseWithPostDetails> getAllCommentsWithPostDetailsByPostId(Pageable pageable) {
        Page<Comment> comments = commentRepository.findAll(pageable);

        return comments.map(commentMapper::toDto5);
    }

    @Transactional
    public long getCountCommentsByPostId(UUID postId) {
        return commentRepository.countAllByPost_Id(postId);
    }

    @Transactional
    public CommentRespond updateCommentContent(UUID commentId, UpdateCommentContentDto updateCommentContentDto) {
        Comment original = findById(commentId);
        Comment oldComment = commentMapper.cloneComment(original);

        if (isNotAllowToWrite(original)) {
            log.info("Not allow to update comment");
            throw new CommentNotFoundException(CommentErrorEnums.UNAUTHORIZED_ACCESS.getMessage());
        }

        Comment newComment = commentMapper.partialUpdate(updateCommentContentDto, original);

        Comment updatedComment = commentRepository.save(newComment);
        log.info("Comment updated for commentId: {}", newComment.getId());

        String userAgent = Objects.requireNonNull(HttpRequestUtils.getCurrentHttpRequest()).getHeader("User-Agent");
        String clientIp = HttpRequestUtils.getClientIpAddress(HttpRequestUtils.getCurrentHttpRequest());


        eventPublisher.publishEvent(new CommentUpdated(
                this,
                newComment.getId(),
                newComment.getPost().getId(),
                newComment.getUser().getId(),
                newComment.getContent(),
                ObjectHelper.extractChangesWithCommonsLang(
                        commentMapper.toDto1(oldComment),
                        commentMapper.toDto1(newComment)
                ),
                userAgent,
                clientIp,
                authService.getUserIdFromContext()
        ));

        return commentMapper.toDto(updatedComment);
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
    public void pinComment(UUID commentId) {
        Comment comment = findById(commentId);
        if (isNotAllowToWrite(comment)) {
            log.info("Not allow to pin comment");
            throw new CommentNotFoundException(CommentErrorEnums.UNAUTHORIZED_ACCESS.getMessage());
        }

        removePinnedCommentByPostId(comment.getPost().getId());

        comment.setIsPinned(true);
        commentRepository.save(comment);
        log.debug("Comment pinned: {}", comment.getId());

        eventPublisher.publishEvent(new CommentPinned(
                this,
                comment.getId(),
                comment.getPost().getId(),
                authService.getUserIdFromContext(),
                comment.getUser().getId()
        ));
    }

    @Transactional
    public void unpinComment(UUID commentId) {
        Comment comment = findById(commentId);
        if (isNotAllowToWrite(comment)) {
            log.info("Not allow to unpin comment");
            throw new CommentNotFoundException(CommentErrorEnums.UNAUTHORIZED_ACCESS.getMessage());
        }

        comment.setIsPinned(false);
        commentRepository.save(comment);
        log.debug("Comment unpinned: {}", comment.getId());

        eventPublisher.publishEvent(new CommentUnPinned(
                this,
                comment.getId(),
                comment.getPost().getId(),
                authService.getUserIdFromContext(),
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

    @Transactional
    public void removePinnedCommentByPostId(UUID postId) {
        commentRepository.removePinnedCommentInPost(postId);
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

    public Map<UUID, Long> getCountCommentsByPostIds(List<UUID> idList) {
        List<Object[]> comments = commentRepository.countCommentsByIds(idList);

        Map<UUID, Long> commentMap = new HashMap<>();

        for (Object[] comment : comments) {
            commentMap.put((UUID) comment[0], (Long) comment[1]);
        }

        return commentMap;
    }

    @Transactional
    public Page<CommentRespond> getAllComments(Pageable pageable) {
        return commentRepository.findAll(pageable).map(commentMapper::toDto);
    }

    @Transactional
    public void bulkDeleteComments(BulkCommentDeleteRequest request) {
        commentRepository.deleteAllById(request.commentIds());
        eventPublisher.publishEvent(new BulkCommentsDeletedEvent(this, request.commentIds()));
    }

    @Transactional
    public List<CommentRespond> bulkUpdateComments(BulkCommentUpdateRequest req) {
        List<Comment> comments = commentRepository.findAllById(req.ids());
        comments.forEach(comment -> {
            if (req.status() != null) {
                comment.setStatus(req.status());
            }
        });
        List<Comment> updatedComments = commentRepository.saveAll(comments);
        eventPublisher.publishEvent(new BulkCommentsUpdatedEvent(this, req.ids(), req.status()));
        return updatedComments.stream().map(commentMapper::toDto).collect(java.util.stream.Collectors.toList());
    }
}
