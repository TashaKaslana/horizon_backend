package org.phong.horizon.admin.notification.services;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.phong.horizon.admin.notification.events.BulkDeletedAdminNotificationEvent;
import org.phong.horizon.admin.notification.events.BulkUpdatedAdminNotificationEvent;
import org.phong.horizon.admin.notification.infrastructure.dtos.AdminNotificationDto;
import org.phong.horizon.admin.notification.infrastructure.dtos.AdminNotificationFilterDto;
import org.phong.horizon.admin.notification.infrastructure.dtos.BulkAdminNotificationDeleteRequest;
import org.phong.horizon.admin.notification.infrastructure.dtos.BulkAdminNotificationUpdateRequest;
import org.phong.horizon.admin.notification.infrastructure.dtos.CreateAdminNotification;
import org.phong.horizon.admin.notification.exceptions.NotificationNotFoundException;
import org.phong.horizon.admin.notification.infrastructure.entities.AdminNotification;
import org.phong.horizon.admin.notification.mappers.AdminNotificationMapper;
import org.phong.horizon.admin.notification.infrastructure.repositories.AdminNotificationRepository;
import org.phong.horizon.admin.notification.infrastructure.specifications.AdminNotificationSpecs;
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
public class AdminNotificationService {

    private final AdminNotificationRepository adminNotificationRepository;
    private final AdminNotificationMapper adminNotificationMapper;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Transactional(readOnly = true)
    public Page<AdminNotificationDto> getAllNotifications(Pageable pageable, AdminNotificationFilterDto filterDto) {
        Specification<AdminNotification> spec = Specification.where(AdminNotificationSpecs.hasType(filterDto.getType()))
                .and(AdminNotificationSpecs.hasSeverity(filterDto.getSeverity()))
                .and(AdminNotificationSpecs.isRead(filterDto.getIsRead()))
                .and(AdminNotificationSpecs.createdAfter(filterDto.getCreatedAfter()))
                .and(AdminNotificationSpecs.createdBefore(filterDto.getCreatedBefore()))
                .and(AdminNotificationSpecs.titleContains(filterDto.getTitle()))
                .and(AdminNotificationSpecs.messageContains(filterDto.getMessage()));
        return adminNotificationRepository.findAll(spec, pageable).map(adminNotificationMapper::toDto);
    }

    @Transactional(readOnly = true)
    public AdminNotificationDto getNotificationById(UUID id) {
        return adminNotificationRepository.findById(id)
                .map(adminNotificationMapper::toDto)
                .orElseThrow(() -> new NotificationNotFoundException(id.toString()));
    }

    @Transactional
    public AdminNotificationDto createNotification(CreateAdminNotification createAdminNotificationDto) {
        AdminNotification notification = adminNotificationMapper.toEntity(createAdminNotificationDto);
        if (notification.getIsRead() == null) {
            notification.setIsRead(false);
        }
        return adminNotificationMapper.toDto(adminNotificationRepository.save(notification));
    }

    @Transactional
    public AdminNotificationDto markAsRead(UUID id) {
        AdminNotification notification = adminNotificationRepository.findById(id)
                .orElseThrow(() -> new NotificationNotFoundException(id.toString()));
        notification.setIsRead(true);
        AdminNotificationDto mapperDto = adminNotificationMapper.toDto(adminNotificationRepository.save(notification));
        applicationEventPublisher.publishEvent(new BulkUpdatedAdminNotificationEvent(this, List.of(id), true));
        return mapperDto;
    }

    @Transactional
    public AdminNotificationDto markAsUnread(UUID id) {
        AdminNotification notification = adminNotificationRepository.findById(id)
                .orElseThrow(() -> new NotificationNotFoundException(id.toString()));
        notification.setIsRead(false);
        AdminNotificationDto mapperDto = adminNotificationMapper.toDto(adminNotificationRepository.save(notification));
        applicationEventPublisher.publishEvent(new BulkUpdatedAdminNotificationEvent(this, List.of(id), false));
        return mapperDto;
    }

    @Transactional
    public void deleteNotification(UUID id) {
        if (!adminNotificationRepository.existsById(id)) {
            throw new NotificationNotFoundException(id.toString());
        }
        adminNotificationRepository.deleteById(id);
        applicationEventPublisher.publishEvent(new BulkDeletedAdminNotificationEvent(this, List.of(id)));
    }

    @Transactional
    public void bulkDeleteNotifications(@Valid BulkAdminNotificationDeleteRequest request) {
        adminNotificationRepository.deleteAllById(request.notificationIds());
        applicationEventPublisher.publishEvent(new BulkDeletedAdminNotificationEvent(this, request.notificationIds()));
    }

    /**
     * Bulk update notifications' read status
     *
     * @param request The bulk update request with notification IDs and new read status
     * @return List of updated notification DTOs
     */
    @Transactional
    public List<AdminNotificationDto> bulkUpdateNotifications(BulkAdminNotificationUpdateRequest request) {
        List<AdminNotification> notifications = adminNotificationRepository.findAllById(request.notificationIds());

        notifications.forEach(notification -> notification.setIsRead(request.isRead()));

        List<AdminNotification> updatedNotifications = adminNotificationRepository.saveAll(notifications);
        applicationEventPublisher.publishEvent(new BulkUpdatedAdminNotificationEvent(this, request.notificationIds(), request.isRead()));
        return updatedNotifications.stream()
            .map(adminNotificationMapper::toDto)
            .collect(java.util.stream.Collectors.toList());
    }
}
