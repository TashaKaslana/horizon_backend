package org.phong.horizon.user.infrastructure.mapstruct;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.phong.horizon.user.dtos.UserCloneDto;
import org.phong.horizon.user.dtos.UserCreateDto;
import org.phong.horizon.user.dtos.UserCreatedDto;
import org.phong.horizon.user.dtos.UserRespondDto;
import org.phong.horizon.user.dtos.UserSummaryRespond;
import org.phong.horizon.user.dtos.UserUpdateDto;
import org.phong.horizon.user.infrastructure.persistence.entities.User;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface UserMapper {
    User toEntity(UserRespondDto userRespondDto);

    UserRespondDto toDto(User user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    User partialUpdate(UserRespondDto userRespondDto, @MappingTarget User user);

    User toEntity(UserUpdateDto userUpdateDto);

    UserUpdateDto toDto1(User user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    User partialUpdate(UserUpdateDto userUpdateDto, @MappingTarget User user);

    User toEntity(UserCreateDto userCreateDto);

    UserCreateDto toDto2(User user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    User partialUpdate(UserCreateDto userCreateDto, @MappingTarget User user);

    User toEntity(UserSummaryRespond userSummaryRespond);

    UserSummaryRespond toDto3(User user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    User partialUpdate(UserSummaryRespond userSummaryRespond, @MappingTarget User user);

    User toEntity(UserCreatedDto userCreatedDto);

    UserCreatedDto toDto4(User user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    User partialUpdate(UserCreatedDto userCreatedDto, @MappingTarget User user);

    UserCloneDto toCloneDto(User user);

    User cloneUser(User user);
}