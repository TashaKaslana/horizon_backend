package org.phong.horizon.user.infrastructure.mapstruct;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.phong.horizon.user.dtos.UserAccountUpdate;
import org.phong.horizon.user.dtos.UserCloneDto;
import org.phong.horizon.user.dtos.UserCreateDto;
import org.phong.horizon.user.dtos.UserCreatedDto;
import org.phong.horizon.user.dtos.UserIntroduction;
import org.phong.horizon.user.dtos.UserRespondDto;
import org.phong.horizon.user.dtos.UserSummaryRespond;
import org.phong.horizon.user.dtos.UserUpdateInfoDto;
import org.phong.horizon.user.infrastructure.persistence.entities.User;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface UserMapper {
    User toEntity(UserRespondDto userRespondDto);

    UserRespondDto toDto(User user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    User partialUpdate(UserRespondDto userRespondDto, @MappingTarget User user);

    User toEntity(UserUpdateInfoDto userUpdateInfoDto);

    UserUpdateInfoDto toDto1(User user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    User partialUpdate(UserUpdateInfoDto userUpdateInfoDto, @MappingTarget User user);

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

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    User partialUpdate(UserAccountUpdate userAccountUpdate, @MappingTarget User user);

    User toEntity(UserIntroduction userIntroduction);

    UserIntroduction toDto5(User user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    User partialUpdate(UserIntroduction userIntroduction, @MappingTarget User user);
}