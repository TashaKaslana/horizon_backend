package org.phong.horizon.admin.notification.services;

import lombok.RequiredArgsConstructor;
import org.phong.horizon.admin.notification.infrastructure.dtos.AdminNotificationDto;
import org.phong.horizon.admin.notification.infrastructure.dtos.AdminNotificationFilterDto;
import org.phong.horizon.admin.notification.exceptions.NotificationNotFoundException;
import org.phong.horizon.admin.notification.infrastructure.entities.AdminNotification;
import org.phong.horizon.admin.notification.mappers.AdminNotificationMapper;
import org.phong.horizon.admin.notification.infrastructure.repositories.AdminNotificationRepository;
import org.phong.horizon.admin.notification.infrastructure.specifications.AdminNotificationSpecs;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminNotificationService {

    private final AdminNotificationRepository adminNotificationRepository;
    private final AdminNotificationMapper adminNotificationMapper;

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
    public AdminNotificationDto createNotification(AdminNotificationDto adminNotificationDto) {
        AdminNotification notification = adminNotificationMapper.toEntity(adminNotificationDto);
        // Ensure isRead is false for new notifications if not explicitly set
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
        return adminNotificationMapper.toDto(adminNotificationRepository.save(notification));
    }

    @Transactional
    public AdminNotificationDto markAsUnread(UUID id) {
        AdminNotification notification = adminNotificationRepository.findById(id)
                .orElseThrow(() -> new NotificationNotFoundException(id.toString()));
        notification.setIsRead(false);
        return adminNotificationMapper.toDto(adminNotificationRepository.save(notification));
    }

    @Transactional
    public void deleteNotification(UUID id) {
        if (!adminNotificationRepository.existsById(id)) {
            throw new NotificationNotFoundException(id.toString());
        }
        adminNotificationRepository.deleteById(id);
    }
}

