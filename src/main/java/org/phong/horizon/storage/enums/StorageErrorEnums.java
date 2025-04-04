package org.phong.horizon.storage.enums;

import lombok.Getter;

@Getter
public enum StorageErrorEnums {
    MISSING_ASSET_PROPERTIES("Required upload data (publicId, secureUrl, resourceType) is missing."),
    ASSET_ALREADY_EXISTS("Asset already exists: "),
    ASSET_NOT_FOUND("Asset not found with ID: "),
    CLOUDINARY_DELETION_FAILED("Failed to delete asset from Cloudinary for ID: "),
    UNEXPECTED_CLOUDINARY_ERROR("Unexpected error during Cloudinary deletion for ID: "),
    DB_DELETION_FAILED("Failed to delete asset record from database for ID: "),
    INVALID_PUBLIC_ID("Public ID cannot be null or blank for Cloudinary deletion."),
    CLOUDINARY_SIGNATURE_FAILED("Could not generate upload signature.");

    private final String message;

    StorageErrorEnums(String message) {
        this.message = message;
    }

}