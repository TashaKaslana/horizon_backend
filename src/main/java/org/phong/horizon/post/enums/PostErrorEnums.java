package org.phong.horizon.post.enums;

import lombok.Getter;

@Getter
public enum PostErrorEnums {
    // Validation Errors
    INVALID_POST_ID("POST_001", "post.error.invalid_post_id"),
    INVALID_USER_ID("POST_002", "post.error.invalid_user_id"),
    INVALID_VIDEO_URL("POST_003", "post.error.invalid_video_url"),
    INVALID_VISIBILITY("POST_004", "post.error.invalid_visibility"),
    INVALID_DURATION("POST_005", "post.error.invalid_duration"),

    // Permission Errors
    POST_NOT_FOUND("POST_006", "post.error.post_not_found"),
    UNAUTHORIZED_POST_ACCESS("POST_007", "post.error.unauthorized_access"),
    UNAUTHORIZED_POST_UPDATE("POST_008", "post.error.unauthorized_update"),
    UNAUTHORIZED_POST_DELETE("POST_009", "post.error.unauthorized_delete"),

    // Business Logic Errors
    POST_CREATION_FAILED("POST_010", "post.error.creation_failed"),
    POST_UPDATE_FAILED("POST_011", "post.error.update_failed"),
    POST_DELETION_FAILED("POST_012", "post.error.deletion_failed"),
    TAG_LIMIT_EXCEEDED("POST_013", "post.error.tag_limit_exceeded"),
    POST_VIDEO_PROCESSING_ERROR("POST_014", "post.error.video_processing_error"),
    POST_ASSET_ALREADY_EXISTS("POST_015", "post.error.asset_already_exists"),
    POST_ASSET_NOT_FOUND("POST_016", "post.error.asset_not_found"),

    POST_CATEGORY_NOT_FOUND("POST_017", "post.error.category_not_found"),
    POST_CATEGORY_EXISTS("POST_018", "post.error.category_exists"),;


    private final String code;
    private final String messageKey;

    PostErrorEnums(String code, String messageKey) {
        this.code = code;
        this.messageKey = messageKey;
    }

    public String getMessage(Object... args) {
        return org.phong.horizon.core.config.LocalizationProvider.getMessage(this.messageKey, args);
    }
}

