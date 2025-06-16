package org.phong.horizon.storage.enums;

import lombok.Getter;

@Getter
public enum StorageErrorEnums {
    MISSING_ASSET_PROPERTIES("storage.error.missing_asset_properties"),
    ASSET_ALREADY_EXISTS("storage.error.asset_already_exists"),
    ASSET_NOT_FOUND("storage.error.asset_not_found"),
    CLOUDINARY_DELETION_FAILED("storage.error.cloudinary_deletion_failed"),
    UNEXPECTED_CLOUDINARY_ERROR("storage.error.unexpected_cloudinary_error"),
    DB_DELETION_FAILED("storage.error.db_deletion_failed"),
    INVALID_PUBLIC_ID("storage.error.invalid_public_id"),
    CLOUDINARY_SIGNATURE_FAILED("storage.error.cloudinary_signature_failed");

    private final String messageKey;

    StorageErrorEnums(String messageKey) {
        this.messageKey = messageKey;
    }

    public String getMessage(Object... args) {
        return org.phong.horizon.core.config.LocalizationProvider.getMessage(this.messageKey, args);
    }

}