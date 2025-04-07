package org.phong.horizon.follow.infrastructure.mapstruct;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.phong.horizon.follow.dtos.FollowOneSideRespond;
import org.phong.horizon.follow.dtos.FollowRespond;
import org.phong.horizon.follow.infrastructure.persistence.entities.Follow;
import org.phong.horizon.user.infrastructure.mapstruct.UserMapper;
import org.phong.horizon.user.services.UserService;

@Mapper(
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        uses = {UserMapper.class, UserService.class}
)
public interface FollowMapper {
    Follow toEntity(FollowRespond followRespond);

    FollowRespond toDto(Follow follow);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Follow partialUpdate(FollowRespond followRespond, @MappingTarget Follow follow);

    Follow toEntity(FollowOneSideRespond followOneSideRespond);

    @Mapping(source = "follower", target = "user")
    @Mapping(source = "createdAt", target = "createdAt")
    FollowOneSideRespond mapToFollowerSide(Follow follow);

    @Mapping(source = "following", target = "user")
    @Mapping(source = "createdAt", target = "createdAt")
    FollowOneSideRespond mapToFollowingSide(Follow follow);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Follow partialUpdate(FollowOneSideRespond followOneSideRespond, @MappingTarget Follow follow);
}