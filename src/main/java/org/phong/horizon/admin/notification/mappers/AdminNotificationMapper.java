package org.phong.horizon.admin.notification.mappers;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.phong.horizon.admin.notification.infrastructure.dtos.CreateAdminNotification;
import org.phong.horizon.admin.notification.infrastructure.dtos.AdminNotificationDto;
import org.phong.horizon.admin.notification.infrastructure.entities.AdminNotification;
import org.phong.horizon.user.infrastructure.mapstruct.UserMapper;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {UserMapper.class}
)
public interface AdminNotificationMapper {
    AdminNotificationDto toDto(AdminNotification entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    AdminNotification toEntity(AdminNotificationDto dto);

    AdminNotification toEntity(CreateAdminNotification createAdminNotification);

    CreateAdminNotification toDto1(AdminNotification adminNotification);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    AdminNotification partialUpdate(CreateAdminNotification createAdminNotification, @MappingTarget AdminNotification adminNotification);
}
