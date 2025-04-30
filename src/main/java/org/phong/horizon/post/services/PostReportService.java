package org.phong.horizon.post.services;

import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.phong.horizon.core.services.AuthService;
import org.phong.horizon.post.dtos.PostReportResponse;
import org.phong.horizon.post.dtos.PostReportRequest;
import org.phong.horizon.post.enums.PostReportErrors;
import org.phong.horizon.post.exceptions.PostReportNotFoundException;
import org.phong.horizon.post.infrastructure.mapstruct.PostReportMapper;
import org.phong.horizon.post.infrastructure.persistence.entities.Post;
import org.phong.horizon.post.infrastructure.persistence.entities.PostReport;
import org.phong.horizon.post.infrastructure.persistence.repositories.PostReportRepository;
import org.phong.horizon.user.infrastructure.persistence.entities.User;
import org.phong.horizon.user.services.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
@Service
public class PostReportService {
    PostReportRepository reportRepo;
    PostService postService;
    UserService userService;
    AuthService authService;
    PostReportMapper postReportMapper;

    @Transactional
    public PostReportResponse reportPost(UUID postId, PostReportRequest request) {
        Post post = postService.getRefById(postId);
        User user = userService.getRefById(authService.getUserIdFromContext());

        PostReport report = postReportMapper.toEntity(request);
        report.setPost(post);
        report.setUser(user);
        report.setReason(request.reason());

        PostReport saved = reportRepo.save(report);

        return postReportMapper.toDto1(saved);
    }

    @Transactional
    public PostReportResponse getReportById(UUID id) {
        PostReport report = reportRepo.findById(id)
                .orElseThrow(() -> new PostReportNotFoundException(PostReportErrors.POST_REPORT_NOT_FOUND.getMessage()));
        return postReportMapper.toDto1(report);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public Page<PostReportResponse> getAllReports(Pageable pageable) {
        return reportRepo.findAll(pageable).map(postReportMapper::toDto1);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteReport(UUID id) {
        if (!reportRepo.existsById(id)) {
            throw new PostReportNotFoundException(PostReportErrors.POST_REPORT_NOT_FOUND.getMessage());
        }

        reportRepo.deleteById(id);
    }
}
