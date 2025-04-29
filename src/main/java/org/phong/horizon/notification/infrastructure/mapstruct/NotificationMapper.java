package org.phong.horizon.notification.infrastructure.mapstruct;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.phong.horizon.notification.dtos.CreateNotificationRequest;
import org.phong.horizon.notification.dtos.NotificationResponse;
import org.phong.horizon.notification.dtos.UpdateNotificationDto;
import org.phong.horizon.notification.infrastructure.persistence.entities.Notification;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface NotificationMapper {
    Notification toEntity(CreateNotificationRequest createNotificationRequest);

    @Mapping(source = "comment.id", target = "commentId")
    @Mapping(source = "post.id", target = "postId")
    @Mapping(source = "recipientUser.id", target = "recipientUserId")
    CreateNotificationRequest toDto(Notification notification);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Notification partialUpdate(CreateNotificationRequest createNotificationRequest, @MappingTarget Notification notification);

    Notification toEntity(UpdateNotificationDto updateNotificationDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Notification partialUpdate(UpdateNotificationDto updateNotificationDto, @MappingTarget Notification notification);

    NotificationResponse toDto2(Notification notification);
}