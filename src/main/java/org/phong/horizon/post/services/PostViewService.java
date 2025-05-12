package org.phong.horizon.post.services;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.phong.horizon.core.services.AuthService;
import org.phong.horizon.core.utils.HttpRequestUtils;
import org.phong.horizon.post.infrastructure.persistence.entities.PostView;
import org.phong.horizon.post.infrastructure.persistence.repositories.ViewRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PostViewService {
    ViewRepository viewRepository;
    AuthService authService;

    public void handleView(UUID postId) {
        HttpServletRequest request = HttpRequestUtils.getCurrentHttpRequest();
        if (request == null) return;

        String ipAddress = HttpRequestUtils.getClientIpAddress(request);
        UUID userId = authService.getUserIdFromContext();

        if (shouldRecordView(postId, userId, ipAddress)) {
            saveView(postId, userId, ipAddress);
        }
    }

    public long getAllTotalViewsByUserId(UUID userId) {
        return viewRepository.countAllByUserIdIs(userId);
    }

    public long getTotalViews(UUID postId) {
        return viewRepository.countByPostId(postId);
    }

    public Map<UUID, Long> getTotalListView(List<UUID> postIds) {
        List<Object[]> views = viewRepository.countViewsByPostIds(postIds);

        Map<UUID, Long> viewMap = new HashMap<>();

        for (Object[] view : views) {
            viewMap.put((UUID) view[0], (Long) view[1]);
        }

        return viewMap;
    }

    private boolean shouldRecordView(UUID postId, UUID userId, String ipAddress) {
        LocalDateTime windowStart = LocalDateTime.now().minusMinutes(10);

        if (userId != null) {
            return !viewRepository.existsByPostIdAndUserIdAndViewedAtAfter(postId, userId, windowStart);
        } else {
            return !viewRepository.existsByPostIdAndIpAddressAndViewedAtAfter(postId, ipAddress, windowStart);
        }
    }

    private void saveView(UUID postId, UUID userId, String ipAddress) {
        PostView view = PostView.builder()
                .postId(postId)
                .userId(userId)
                .ipAddress(ipAddress)
                .build();
        viewRepository.save(view);
    }
}
