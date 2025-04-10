package org.phong.horizon.storage.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.phong.horizon.storage.dtos.AssetRespond;
import org.phong.horizon.storage.dtos.UploadCompleteRequest;
import org.phong.horizon.storage.enums.StorageErrorEnums;
import org.phong.horizon.storage.exceptions.AssetAlreadyExistsException;
import org.phong.horizon.storage.exceptions.AssetNotFoundException;
import org.phong.horizon.storage.exceptions.CloudinaryException;
import org.phong.horizon.storage.exceptions.MissingAssetPropertiesException;
import org.phong.horizon.storage.infrastructure.cloudinary.CloudinaryClientService;
import org.phong.horizon.storage.infrastructure.mapper.AssetMapper;
import org.phong.horizon.storage.infrastructure.persistence.entities.Asset;
import org.phong.horizon.storage.infrastructure.persistence.repositories.AssetRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
@AllArgsConstructor
public class StorageService {
    private final AssetRepository assetRepository;
    private final CloudinaryClientService cloudinaryClientService;
    private final CloudinaryUrlGenerator cloudinaryUrlGenerator;
    private final AssetMapper assetMapper;

    @Transactional
    public AssetRespond createAsset(UploadCompleteRequest uploadData) {
        if (uploadData == null || uploadData.publicId() == null || uploadData.secureUrl() == null || uploadData.resourceType() == null) {
            throw new MissingAssetPropertiesException(StorageErrorEnums.MISSING_ASSET_PROPERTIES.getMessage());
        }

        if (assetRepository.findByPublicId(uploadData.publicId()).isPresent()) {
            log.warn("Asset with public ID '{}' already exists in DB. Ignoring duplicate creation request.", uploadData.publicId());
            throw new AssetAlreadyExistsException(StorageErrorEnums.ASSET_ALREADY_EXISTS.getMessage() + uploadData.publicId());
        }

        Asset asset = assetMapper.toEntity(uploadData);
        Asset savedAsset = assetRepository.save(asset);
        log.info("Saved new asset record to DB. ID: {}, Public ID: {}", savedAsset.getId(), savedAsset.getPublicId());

        return assetMapper.toDto1(savedAsset);
    }

    public AssetRespond getAssetById(UUID id) {
        return assetMapper.toDto1(findAssetById(id));
    }

    public AssetRespond getAssetByPublicId(String publicId) {
        return assetMapper.toDto1(findAssetByPublicId(publicId));
    }

    public Asset findAssetById(UUID id) {
        return assetRepository.findById(id).orElseThrow(
                () -> new AssetNotFoundException(StorageErrorEnums.ASSET_NOT_FOUND.getMessage() + id)
        );
    }

    public Asset getRefById(UUID id) {
        return assetRepository.getReferenceById(id);
    }

    public Asset findAssetByPublicId(String publicId) {
        return assetRepository.findByPublicId(publicId).orElseThrow(
                () -> new AssetNotFoundException(StorageErrorEnums.ASSET_NOT_FOUND.getMessage() + publicId)
        );
    }

    public String generateVideoPlaybackUrl(String publicId) {
        return cloudinaryUrlGenerator.generateVideoPlaybackUrl(
                Asset.builder().publicId(publicId).build()
        );
    }

    public String generateVideoThumbnailUrl(String publicId) {
        return cloudinaryUrlGenerator.generateVideoThumbnailUrl(
                Asset.builder().publicId(publicId).build()
        );
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or @authService.isPrincipal(assetRepository.getCreatedByIdByPublicId(id))")
    @Transactional
    public void deleteAsset(UUID id) {
        log.info("Request received to delete asset with DB ID: {}", id);

        Asset asset = assetRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Asset not found in DB with ID: {}", id);
                    return new AssetNotFoundException(StorageErrorEnums.ASSET_NOT_FOUND.getMessage() + id);
                });

        try {
            log.info("Attempting deletion on Cloudinary for Public ID: {}", asset.getPublicId());
            cloudinaryClientService.deleteAssetOnCloudinary(asset.getPublicId(), asset.getResourceType());
            log.info("Cloudinary deletion request successful or asset was already gone for Public ID: {}", asset.getPublicId());
        } catch (IOException e) {
            log.error("Cloudinary API deletion failed for Public ID: {}. Error: {}", asset.getPublicId(), e.getMessage());
            throw new CloudinaryException(StorageErrorEnums.CLOUDINARY_DELETION_FAILED.getMessage() + id, e);
        } catch (Exception e) {
            log.error("Unexpected error during Cloudinary deletion via client service for Public ID: {}. Error: {}", asset.getPublicId(), e.getMessage());
            throw new CloudinaryException(StorageErrorEnums.UNEXPECTED_CLOUDINARY_ERROR.getMessage() + id, e);
        }

        try {
            assetRepository.delete(asset);
            log.info("Successfully deleted asset record from DB. ID: {}, Public ID: {}", asset.getId(), asset.getPublicId());
        } catch (Exception e) {
            log.error("Failed to delete asset record from DB for ID: {}. Public ID: {}", asset.getId(), asset.getPublicId(), e);
            throw new CloudinaryException(StorageErrorEnums.DB_DELETION_FAILED.getMessage() + id, e);
        }
    }

    public Map<String, Object> generateUploadSignature(Map<String, Object> paramsFromClient) {
        return cloudinaryClientService.generateUploadSignature(paramsFromClient);
    }
}