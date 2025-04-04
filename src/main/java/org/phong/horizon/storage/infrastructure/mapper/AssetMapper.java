package org.phong.horizon.storage.infrastructure.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.phong.horizon.storage.dtos.AssetRespond;
import org.phong.horizon.storage.dtos.UploadCompleteRequest;
import org.phong.horizon.storage.infrastructure.persistence.entities.Asset;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface AssetMapper {
    Asset toEntity(UploadCompleteRequest uploadCompleteRequest);

    UploadCompleteRequest toDto(Asset asset);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Asset partialUpdate(UploadCompleteRequest uploadCompleteRequest, @MappingTarget Asset asset);

    Asset toEntity(AssetRespond assetRespond);

    AssetRespond toDto1(Asset asset);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Asset partialUpdate(AssetRespond assetRespond, @MappingTarget Asset asset);
}