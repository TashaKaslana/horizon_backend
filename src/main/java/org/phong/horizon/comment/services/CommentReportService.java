package org.phong.horizon.comment.services;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.phong.horizon.comment.dtos.CreateReportComment;
import org.phong.horizon.comment.dtos.ReportCommentResponse;
import org.phong.horizon.comment.events.ReportCommentCreated;
import org.phong.horizon.comment.exceptions.ReportCommentNotFoundException;
import org.phong.horizon.comment.infrastructure.mapstruct.ReportCommentMapper;
import org.phong.horizon.comment.infrastructure.persistence.entities.Comment;
import org.phong.horizon.comment.infrastructure.persistence.entities.ReportComment;
import org.phong.horizon.comment.infrastructure.persistence.repositories.ReportCommentRepository;
import org.phong.horizon.core.services.AuthService;
import org.phong.horizon.user.infrastructure.persistence.entities.User;
import org.phong.horizon.user.services.UserService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CommentReportService {
    CommentService commentService;
    ReportCommentRepository repository;
    AuthService authService;
    UserService userService;
    ReportCommentMapper mapper;
    ApplicationEventPublisher eventPublisher;

    @Transactional
    public void reportComment(CreateReportComment request) {
        ReportComment reportComment = mapper.toEntity(request);
        Comment comment = commentService.getRefById(request.commentId());
        User user = userService.getRefById(authService.getUserIdFromContext());

        reportComment.setUser(user);
        reportComment.setComment(comment);
        log.debug("Comment with ID: {} reported for reason: {}", request.commentId(), request.reason());

        repository.save(reportComment);
        eventPublisher.publishEvent(new ReportCommentCreated(this,
                reportComment.getId(), comment.getId(), user.getId(), comment.getUser().getId(), request.reason())
        );
    }

    @Transactional
    public void deleteReportComment(UUID reportCommentId) {
        ReportComment reportComment = repository.findById(reportCommentId)
                .orElseThrow(() -> new ReportCommentNotFoundException("Report comment not found"));
        repository.delete(reportComment);
    }

    @Transactional(readOnly = true)
    public ReportCommentResponse getReportCommentById(UUID reportCommentId) {
        ReportComment reportComment = repository.findById(reportCommentId)
                .orElseThrow(() -> new ReportCommentNotFoundException("Report comment not found"));

        return mapper.toDto1(reportComment);
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    public Page<ReportCommentResponse> getAllReportComments(Pageable pageable) {
        return repository.findAll(pageable).map(mapper::toDto1);
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR') or @userId == authentication.principal.id")
    public Page<ReportCommentResponse> getReportCommentsByUserId(UUID userId, Pageable pageable) {
        return repository.findAllByUser_Id(userId, pageable).map(mapper::toDto1);
    }
}
