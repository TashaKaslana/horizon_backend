package org.phong.horizon.notification.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.phong.horizon.comment.services.CommentService;
import org.phong.horizon.core.services.AuthService;
import org.phong.horizon.notification.dtos.CreateNotificationRequest;
import org.phong.horizon.notification.dtos.NotificationFilterCriteria;
import org.phong.horizon.notification.dtos.NotificationRespond;
import org.phong.horizon.notification.dtos.UpdateNotificationDto;
import org.phong.horizon.notification.enums.NotificationErrorEnum;
import org.phong.horizon.notification.exceptions.NotificationAccessDenialException;
import org.phong.horizon.notification.exceptions.NotificationsNotFoundException;
import org.phong.horizon.notification.infrastructure.mapstruct.NotificationMapper;
import org.phong.horizon.notification.infrastructure.persistence.entities.Notification;
import org.phong.horizon.notification.infrastructure.persistence.repositories.NotificationRepository;
import org.phong.horizon.notification.infrastructure.persistence.repositories.NotificationSpecifications;
import org.phong.horizon.post.services.PostService;
import org.phong.horizon.user.infrastructure.persistence.entities.User;
import org.phong.horizon.user.services.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class NotificationService {
    private static final int DEFAULT_PERMANENT_DELETE_DAYS_THRESHOLD = 7;

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final PostService postService;
    private final UserService userService;
    private final CommentService commentService;
    private final AuthService authService;

    @Transactional(readOnly = true)
    public Page<NotificationRespond> getMyNotifications(
            Pageable pageable,
            NotificationFilterCriteria filters
    ) {
        UUID currentUserId = authService.getUserIdFromContext();

        Specification<Notification> specification = NotificationSpecifications.buildSpecification(
                currentUserId,
                filters
        );

        Page<Notification> notifications = notificationRepository.findAll(specification, pageable);

        return notifications.map(notificationMapper::toDto2);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public Page<NotificationRespond> getAllNotificationByRecipientId(Pageable pageable,
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
    public NotificationRespond getNotificationById(UUID notificationId) {
        Notification notification = findById(notificationId);
        if (isNotAllowedToAccessNotification(notification)) {
            log.warn("User {} tried to get notification {} but was denied", authService.getUserIdFromContext(), notificationId);
            throw new NotificationAccessDenialException(NotificationErrorEnum.NOTIFICATION_ACCESS_DENIED.getMessage());
        }

        return notificationMapper.toDto2(notification);
    }

    //internal service, do not expose to controller
    @Transactional
    public void createEventNotification(UUID senderUserId, CreateNotificationRequest request) {
        try {
            Notification notification = notificationMapper.toEntity(request);

            processToAddNotificationsRelationship(request, notification);

            if (senderUserId != null) {
                notification.setSenderUser(userService.getRefById(senderUserId));
            } else {
                log.warn("Creating notification of type {} without a sender user.", request.getType());
                notification.setSenderUser(null);
            }

            notificationRepository.save(notification);
        } catch (Exception e) {
            log.error("Error while creating notification of type {}", request.getType(), e);
        }
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

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void updateNotificationById(UUID notificationId, UpdateNotificationDto request) {
        Notification notification = findById(notificationId);

        notificationMapper.partialUpdate(request, notification);
        log.info("Updating notification with id {}", notificationId);

        notificationRepository.save(notification);
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

        notificationRepository.save(notification);
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
