package org.phong.horizon.report.services;

import lombok.RequiredArgsConstructor;
import org.phong.horizon.comment.infrastructure.persistence.entities.Comment;
import org.phong.horizon.comment.services.CommentService;
import org.phong.horizon.post.infrastructure.persistence.entities.Post;
import org.phong.horizon.post.services.PostService;
import org.phong.horizon.report.dto.CreateReportRequest;
import org.phong.horizon.report.dto.ReportDto;
import org.phong.horizon.report.enums.ReportErrorCode;
import org.phong.horizon.report.enums.ModerationStatus;
import org.phong.horizon.report.enums.ModerationItemType;
import org.phong.horizon.report.exceptions.InvalidReportInputException;
import org.phong.horizon.report.exceptions.ReportNotFoundException;
import org.phong.horizon.report.infrastructure.persistence.entities.Report;
import org.phong.horizon.report.infrastructure.persistence.repositories.repositories.ReportRepository;
import org.phong.horizon.report.infrastructure.mapper.ReportMapper;
import org.phong.horizon.report.specifications.ReportSpecifications;
import org.phong.horizon.user.infrastructure.persistence.entities.User;
import org.phong.horizon.user.services.UserService;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final ReportRepository reportRepository;
    private final ReportMapper reportMapper;
    private final UserService userService;
    private final PostService postService;
    private final CommentService commentService;

    @Transactional
    public ReportDto createReport(CreateReportRequest request, UUID reporterId) {
        User reporter = userService.findById(reporterId);

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
                break;
            default:
                throw new InvalidReportInputException(ReportErrorCode.INVALID_ITEM_TYPE);
        }

        Report savedReport = reportRepository.save(report);
        return reportMapper.toDto(savedReport);
    }

    @Transactional(readOnly = true)
    public ReportDto getReportById(UUID reportId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new ReportNotFoundException(reportId));
        return reportMapper.toDto(report);
    }

    @Transactional(readOnly = true)
    public List<ReportDto> getAllReports() {
        return reportRepository.findAll().stream()
                .map(reportMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ReportDto> searchReports(
            UUID reporterId,
            UUID reportedUserId,
            ModerationStatus status,
            ModerationItemType itemType,
            UUID itemId
    ) {
        Specification<Report> spec = ReportSpecifications.withDynamicQuery(reporterId, reportedUserId, status, itemType, itemId);
        return reportRepository.findAll(spec).stream()
                .map(reportMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public ReportDto updateReportStatus(UUID reportId, ModerationStatus newStatus) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new ReportNotFoundException(reportId));

        report.setStatus(newStatus);
        return reportMapper.toDto(report);
    }

    @Transactional
    public void deleteReport(UUID reportId) {
        if (!reportRepository.existsById(reportId)) {
            throw new ReportNotFoundException(reportId);
        }
        reportRepository.deleteById(reportId);
    }
}
