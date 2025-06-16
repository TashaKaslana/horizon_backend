package org.phong.horizon.notification.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.phong.horizon.comment.services.CommentService;
import org.phong.horizon.core.services.AuthService;
import org.phong.horizon.notification.dtos.CreateNotificationRequest;
import org.phong.horizon.notification.dtos.NotificationFilterCriteria;
import org.phong.horizon.notification.dtos.NotificationResponse;
import org.phong.horizon.notification.dtos.NotificationStatistic;
import org.phong.horizon.notification.dtos.UpdateNotificationDto;
import org.phong.horizon.notification.enums.NotificationErrorEnum;
import org.phong.horizon.notification.enums.NotificationType;
import org.phong.horizon.notification.events.NotificationDeletedEvent;
import org.phong.horizon.notification.events.NotificationUpdatedEvent;
import org.phong.horizon.notification.exceptions.NotificationAccessDenialException;
import org.phong.horizon.notification.exceptions.NotificationsNotFoundException;
import org.phong.horizon.notification.infrastructure.mapstruct.NotificationMapper;
import org.phong.horizon.notification.infrastructure.persistence.entities.Notification;
import org.phong.horizon.notification.infrastructure.persistence.projections.NotificationStatisticProjection;
import org.phong.horizon.notification.infrastructure.persistence.repositories.NotificationRepository;
import org.phong.horizon.notification.infrastructure.persistence.repositories.NotificationSpecifications;
import org.phong.horizon.notification.utils.NotificationTypeHelper;
import org.phong.horizon.post.dtos.PostSummaryResponse;
import org.phong.horizon.post.infrastructure.mapstruct.PostMapper;
import org.phong.horizon.post.services.PostService;
import org.phong.horizon.user.dtos.UserSummaryRespond;
import org.phong.horizon.user.infrastructure.mapstruct.UserMapper;
import org.phong.horizon.user.infrastructure.persistence.entities.User;
import org.phong.horizon.user.services.UserService;
import org.phong.horizon.core.config.LocalizationProvider;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class NotificationService {
    private static final int DEFAULT_PERMANENT_DELETE_DAYS_THRESHOLD = 7;

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final UserMapper userMapper;
    private final PostMapper postMapper;
    private final PostService postService;
    private final UserService userService;
    private final CommentService commentService;
    private final AuthService authService;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional(readOnly = true)
    public Page<NotificationResponse> getMyNotifications(
            Pageable pageable,
            NotificationFilterCriteria filters
    ) {
        UUID currentUserId = authService.getUserIdFromContext();

        Specification<Notification> specification = NotificationSpecifications.buildSpecification(
                currentUserId,
                filters
        );

        Page<Notification> notifications = notificationRepository.findAll(specification, pageable);

        return notifications.map(notification -> {
            String content = generateNotificationContent(notification);

            NotificationResponse response = notificationMapper.toDto2(notification);
            return new NotificationResponse(
                    response.id(),
                    response.recipientUser(),
                    response.senderUser(),
                    response.post(),
                    response.comment(),
                    content,
                    response.type(),
                    response.extraData(),
                    response.isRead(),
                    response.isDeleted(),
                    response.createdAt(),
                    response.deletedAt()
            );
        });
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public Page<NotificationResponse> getAllNotificationByRecipientId(Pageable pageable,
                                                                      UUID recipientId,
                                                                      NotificationFilterCriteria filters) {
        Specification<Notification> specification = NotificationSpecifications.buildSpecification(
                recipientId,
                filters
        );

        Page<Notification> notifications = notificationRepository.findAll(specification, pageable);

        return notifications.map(notificationMapper::toDto2);
    }

    @Transactional(readOnly = true)
    public NotificationResponse getNotificationById(UUID notificationId) {
        Notification notification = findById(notificationId);
        if (isNotAllowedToAccessNotification(notification)) {
            log.warn("User {} tried to get notification {} but was denied", authService.getUserIdFromContext(), notificationId);
            throw new NotificationAccessDenialException(NotificationErrorEnum.NOTIFICATION_ACCESS_DENIED.getMessage());
        }

        return notificationMapper.toDto2(notification);
    }

    //internal service, do not expose it to controller
    @Transactional
    public UUID createEventNotification(UUID senderUserId, CreateNotificationRequest request) {
        Notification notification = notificationMapper.toEntity(request);
        try {
            processToAddNotificationsRelationship(request, notification);

            if (senderUserId != null) {
                notification.setSenderUser(userService.getRefById(senderUserId));
            } else {
                log.warn("Creating notification of type {} without a sender user.", request.getType());
                notification.setSenderUser(null);
            }
        } catch (Exception e) {
            log.error("Error while creating notification of type {}", request.getType(), e);
        }

        return notificationRepository.save(notification).getId();
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void createAdminNotifications(CreateNotificationRequest request) {
        Notification notification = notificationMapper.toEntity(request);

        processToAddNotificationsRelationship(request, notification);

        UUID currentUserId = authService.getUserIdFromContext();
        User currentUser = userService.getRefById(currentUserId);
        notification.setSenderUser(currentUser);

        notificationRepository.save(notification);
    }

    private void processToAddNotificationsRelationship(CreateNotificationRequest request, Notification notification) {
        notification.setRecipientUser(userService.getRefById(request.getRecipientUserId()));

        if (request.getPostId() != null) {
            notification.setPost(postService.getRefById(request.getPostId()));
        }

        if (request.getCommentId() != null) {
            notification.setComment(commentService.getRefById(request.getCommentId()));
        }
    }

    private String generateNotificationContent(Notification notification) {
        UserSummaryRespond sender = userMapper.toDto3(notification.getSenderUser());
        NotificationType type = notification.getType();

        return switch (type) {
            case LIKE_POST -> LocalizationProvider.getMessage("notification.content.like_post", "@" + sender.username());
            case COMMENT_POST -> LocalizationProvider.getMessage("notification.content.comment_post", "@" + sender.username());
            case LIKE_COMMENT -> LocalizationProvider.getMessage("notification.content.like_comment", "@" + sender.username());
            case REPLY_COMMENT -> LocalizationProvider.getMessage("notification.content.reply_comment", "@" + sender.username());
            case MENTION_COMMENT -> LocalizationProvider.getMessage("notification.content.mention_comment", "@" + sender.username());
            case NEW_FOLLOWER -> LocalizationProvider.getMessage("notification.content.new_follower", "@" + sender.username());
            case UN_FOLLOWER -> LocalizationProvider.getMessage("notification.content.un_follower", "@" + sender.username());
            case REPORT_COMMENT -> LocalizationProvider.getMessage("notification.content.report_comment");
            case REPORT_POST -> LocalizationProvider.getMessage("notification.content.report_post");
            case COMMENT_PINNED -> LocalizationProvider.getMessage("notification.content.comment_pinned");
            case SYSTEM_MESSAGE -> notification.getContent();
        };
    }


    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void updateNotificationById(UUID notificationId, UpdateNotificationDto request) {
        Notification notification = findById(notificationId);

        notificationMapper.partialUpdate(request, notification);
        log.info("Updating notification with id {}", notificationId);
        Notification saved = notificationRepository.save(notification);
        eventPublisher.publishEvent(new NotificationUpdatedEvent(this, notificationMapper.toDto2(saved)));
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void forceDeleteNotificationById(UUID notificationId) {
        Notification notification = findById(notificationId);

        notificationRepository.delete(notification);
    }

    @Transactional
    public void softDeleteNotificationById(UUID notificationId) {
        Notification notification = findById(notificationId);
        if (isNotAllowedToAccessNotification(notification)) {
            log.warn("User {} tried to soft delete notification {} but was denied", authService.getUserIdFromContext(), notificationId);
            throw new NotificationAccessDenialException(NotificationErrorEnum.NOTIFICATION_ACCESS_DENIED.getMessage());
        }

        notification.setIsDeleted(true);
        notification.setDeletedAt(OffsetDateTime.now());

        Notification saved = notificationRepository.save(notification);
        eventPublisher.publishEvent(new NotificationDeletedEvent(this, saved.getId(), saved.getRecipientUser().getId()));
    }

    @Transactional
    public void cleanupOldSoftDeletedNotifications() {
        OffsetDateTime thresholdDate = OffsetDateTime.now().minusDays(DEFAULT_PERMANENT_DELETE_DAYS_THRESHOLD);

        log.info("Attempting to permanently delete notifications soft-deleted before {}", thresholdDate);

        int deletedCount = notificationRepository.deleteOldSoftDeletedNotifications(thresholdDate);

        log.info("Permanently deleted {} notifications that were soft-deleted before {}", deletedCount, thresholdDate);
    }

    @Transactional
    public void markNotificationAsRead(UUID notificationId) {
        Notification notification = findById(notificationId);
        if (isNotAllowedToAccessNotification(notification)) {
            log.warn("User {} tried to mark notification {} but was denied", authService.getUserIdFromContext(), notificationId);
            throw new NotificationAccessDenialException(NotificationErrorEnum.NOTIFICATION_ACCESS_DENIED.getMessage());
        }

        notification.setIsRead(true);

        notificationRepository.save(notification);
    }

    @Transactional
    public void markAllNotificationsAsRead(String type) {
        UUID currentUserId = authService.getUserIdFromContext();
        if (type == null) {
            notificationRepository.markAllNotificationsAsRead(currentUserId, true);
        } else {
            NotificationType notificationType = NotificationType.valueOf(type);
            notificationRepository.markAllNotificationsAsReadByType(currentUserId, notificationType, true);
        }
    }

    @Transactional
    public void unmarkNotificationAsRead(UUID notificationId) {
        Notification notification = findById(notificationId);
        if (isNotAllowedToAccessNotification(notification)) {
            log.warn("User {} tried to unmark notification {} but was denied", authService.getUserIdFromContext(), notificationId);
            throw new NotificationAccessDenialException(NotificationErrorEnum.NOTIFICATION_ACCESS_DENIED.getMessage());
        }

        notification.setIsRead(false);

        notificationRepository.save(notification);
    }

    @Transactional
    public void unmarkAllNotificationsAsRead(String type) {
        UUID currentUserId = authService.getUserIdFromContext();
        if (type == null) {
            notificationRepository.markAllNotificationsAsRead(currentUserId, false);
        } else {
            NotificationType notificationType = NotificationType.valueOf(type);
            notificationRepository.markAllNotificationsAsReadByType(currentUserId, notificationType, false);
        }
    }

    @Transactional
    public void dismissAll(String type) {
        UUID currentUserId = authService.getUserIdFromContext();
        if (type == null) {
            notificationRepository.dismissAll(currentUserId, true);
        } else {
            NotificationType notificationType = NotificationType.valueOf(type);
            notificationRepository.dismissAllByType(currentUserId, notificationType, true);
        }
    }

    public NotificationStatistic getStatistics() {
        UUID currentUserId = authService.getUserIdFromContext();
        List<NotificationStatisticProjection> rawStats = notificationRepository.getNotificationStatisticsByUserId(currentUserId);

        Map<String, NotificationStatistic.NotificationCount> groupedStats = new HashMap<>();

        List<String> allGroupTypes = Arrays.asList("like", "follow", "comment", "mention", "post", "system");
        for (String groupType : allGroupTypes) {
            groupedStats.put(groupType, new NotificationStatistic.NotificationCount(0, 0));
        }

        for (NotificationStatisticProjection stat : rawStats) {
            String groupType = NotificationTypeHelper.getGroupType(stat.getType());

            NotificationStatistic.NotificationCount existing = groupedStats.get(groupType);
            int count = Math.toIntExact(existing.count() + stat.getTotal());
            int unread = Math.toIntExact(existing.unreadCount() + stat.getUnread());

            groupedStats.put(groupType, new NotificationStatistic.NotificationCount(count, unread));
        }

        int totalCount = groupedStats.values().stream().mapToInt(NotificationStatistic.NotificationCount::count).sum();
        int totalUnreadCount = groupedStats.values().stream().mapToInt(NotificationStatistic.NotificationCount::unreadCount).sum();

        return new NotificationStatistic(totalCount, totalUnreadCount, groupedStats);
    }


    @Transactional(readOnly = true)
    protected boolean isNotAllowedToAccessNotification(Notification notification) {
        return !notification.getRecipientUser().getId().equals(authService.getUserIdFromContext());
    }

    @Transactional(readOnly = true)
    public Notification findById(UUID notificationId) {
        return notificationRepository.findById(notificationId).orElseThrow(
                () -> {
                    log.warn("Notification with id {} can't found", notificationId);
                    return new NotificationsNotFoundException(NotificationErrorEnum.NOTIFICATION_NOT_FOUND.getMessage());
                }
        );
    }
}
