package org.phong.horizon.report.services;

import lombok.RequiredArgsConstructor;
import org.phong.horizon.comment.infrastructure.persistence.entities.Comment;
import org.phong.horizon.comment.services.CommentService;
import org.phong.horizon.post.infrastructure.persistence.entities.Post;
import org.phong.horizon.post.services.PostService;
import org.phong.horizon.report.dto.CreateReportRequest;
import org.phong.horizon.report.dto.ReportDto;
import org.phong.horizon.report.enums.ModerationItemType;
import org.phong.horizon.report.enums.ModerationStatus;
import org.phong.horizon.report.enums.ReportErrorCode;
import org.phong.horizon.report.events.ReportCreatedEvent;
import org.phong.horizon.report.events.ReportDeletedEvent;
import org.phong.horizon.report.events.ReportUpdatedEvent;
import org.phong.horizon.report.exceptions.InvalidReportInputException;
import org.phong.horizon.report.exceptions.ReportNotFoundException;
import org.phong.horizon.report.infrastructure.mapper.ReportMapper;
import org.phong.horizon.report.infrastructure.persistence.entities.Report;
import org.phong.horizon.report.infrastructure.persistence.repositories.ReportRepository;
import org.phong.horizon.report.specifications.ReportSpecifications;
import org.phong.horizon.user.infrastructure.persistence.entities.User;
import org.phong.horizon.user.services.UserService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final ReportRepository reportRepository;
    private final ReportMapper reportMapper;
    private final UserService userService;
    private final PostService postService;
    private final CommentService commentService;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public ReportDto createReport(CreateReportRequest request, UUID reporterId) {
        User reporter = userService.findById(reporterId);
        UUID actualReportedUserId;
        UUID reportedItemId;

        Report report = reportMapper.toEntity(request);
        report.setReporter(reporter);

        switch (request.getItemType()) {
            case POST:
                if (request.getPostId() == null) {
                    throw new InvalidReportInputException(ReportErrorCode.MISSING_POST_ID);
                }
                Post post = postService.findPostById(request.getPostId());
                report.setPost(post);
                if (post.getUser().getId().equals(reporterId)) {
                    throw new InvalidReportInputException("Users cannot report their own posts.");
                }
                actualReportedUserId = post.getUser().getId();
                reportedItemId = post.getId();
                break;
            case COMMENT:
                if (request.getCommentId() == null) {
                    throw new InvalidReportInputException(ReportErrorCode.MISSING_COMMENT_ID);
                }
                Comment comment = commentService.findById(request.getCommentId());
                report.setComment(comment);

                if (comment.getUser().getId().equals(reporterId)) {
                    throw new InvalidReportInputException("Users cannot report their own comments.");
                }
                actualReportedUserId = comment.getUser().getId();
                reportedItemId = comment.getId();
                break;
            case USER:
                if (request.getReportedUserId() == null) {
                    throw new InvalidReportInputException(ReportErrorCode.MISSING_USER_ID);
                }
                User reportedUser = userService.findById(request.getReportedUserId());
                report.setReportedUser(reportedUser);
                if (reportedUser.getId().equals(reporterId)) {
                    throw new InvalidReportInputException(ReportErrorCode.CANNOT_REPORT_SELF);
                }
                actualReportedUserId = reportedUser.getId();
                reportedItemId = reportedUser.getId();
                break;
            default:
                throw new InvalidReportInputException(ReportErrorCode.INVALID_ITEM_TYPE);
        }

        Report savedReport = reportRepository.save(report);

        eventPublisher.publishEvent(new ReportCreatedEvent(
                this,
                savedReport.getId(),
                reporterId,
                reportedItemId,
                request.getItemType(),
                actualReportedUserId,
                request.getReason()
        ));

        return reportMapper.toDto(savedReport);
    }

    @Transactional(readOnly = true)
    public ReportDto getReportById(UUID reportId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new ReportNotFoundException(reportId));
        return reportMapper.toDto(report);
    }

    @Transactional(readOnly = true)
    public Page<ReportDto> getAllReports(Pageable pageable) {
        return reportRepository.findAll(pageable)
                .map(reportMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<ReportDto> searchReports(
            UUID reporterId,
            UUID reportedUserId,
            ModerationStatus status,
            ModerationItemType itemType,
            UUID itemId,
            Pageable pageable
    ) {
        Specification<Report> spec = ReportSpecifications.withDynamicQuery(reporterId, reportedUserId, status, itemType, itemId);
        Page<Report> reportPage = reportRepository.findAll(spec, pageable);
        return reportPage.map(reportMapper::toDto);
    }

    @Transactional
    public ReportDto updateReportStatus(UUID reportId, ModerationStatus newStatus) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new ReportNotFoundException(reportId));

        report.setStatus(newStatus);

        // Set resolvedAt timestamp when the report is resolved or action is taken
        if (isResolutionStatus(newStatus)) {
            report.setResolvedAt(java.time.OffsetDateTime.now());
        }

        ReportDto dto = reportMapper.toDto(report);
        eventPublisher.publishEvent(new ReportUpdatedEvent(this, dto));
        return dto;
    }

    /**
     * Checks if a given status represents a resolution or action taken
     *
     * @param status The status to check
     * @return true if the status indicates resolution or action
     */
    private boolean isResolutionStatus(ModerationStatus status) {
        return status != ModerationStatus.REVIEWED_REJECTED;
    }

    @Transactional
    public void deleteReport(UUID reportId) {
        if (!reportRepository.existsById(reportId)) {
            throw new ReportNotFoundException(reportId);
        }
        reportRepository.deleteById(reportId);
        eventPublisher.publishEvent(new ReportDeletedEvent(this, reportId));
    }

    /**
     * Bulk delete reports by IDs
     * @param request Bulk delete request containing report IDs to delete
     */
    @Transactional
    public void bulkDeleteReports(org.phong.horizon.report.dto.BulkReportDeleteRequest request) {
        reportRepository.deleteAllById(request.reportIds());
    }

    /**
     * Bulk update reports status
     * @param request Bulk update request containing report IDs and the new status/notes
     * @return List of updated report DTOs
     */
    @Transactional
    public List<ReportDto> bulkUpdateReportStatus(org.phong.horizon.report.dto.BulkReportUpdateRequest request) {
        List<Report> reports = reportRepository.findAllById(request.reportIds());

        reports.forEach(report -> {
            report.setStatus(request.status());
            if (request.moderatorNotes() != null && !request.moderatorNotes().isBlank()) {
                report.setModeratorNotes(request.moderatorNotes());
            }

            // Set resolvedAt timestamp when the report is resolved or action is taken
            if (isResolutionStatus(request.status())) {
                report.setResolvedAt(java.time.OffsetDateTime.now());
            }
        });

        List<Report> updatedReports = reportRepository.saveAll(reports);
        return updatedReports.stream().map(reportMapper::toDto).collect(java.util.stream.Collectors.toList());
    }
}
