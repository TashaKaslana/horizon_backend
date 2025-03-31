package org.phong.horizon.post.enums;

import lombok.Getter;

@Getter
public enum PostErrorEnums {
    // Validation Errors
    INVALID_POST_ID("POST_001", "Invalid post ID."),
    INVALID_USER_ID("POST_002", "Invalid user ID."),
    INVALID_VIDEO_URL("POST_003", "Video URL cannot be empty."),
    INVALID_VISIBILITY("POST_004", "Visibility must be 'public', 'private', or 'friends'."),
    INVALID_DURATION("POST_005", "Duration must be greater than 0."),

    // Permission Errors
    POST_NOT_FOUND("POST_006", "Post not found."),
    UNAUTHORIZED_POST_ACCESS("POST_007", "You do not have permission to access this post."),
    UNAUTHORIZED_POST_UPDATE("POST_008", "You do not have permission to update this post."),
    UNAUTHORIZED_POST_DELETE("POST_009", "You do not have permission to delete this post."),

    // Business Logic Errors
    POST_CREATION_FAILED("POST_010", "Failed to create post. Please try again."),
    POST_UPDATE_FAILED("POST_011", "Failed to update post. Please try again."),
    POST_DELETION_FAILED("POST_012", "Failed to delete post. Please try again."),
    TAG_LIMIT_EXCEEDED("POST_013", "You can only add up to 10 tags."),
    POST_VIDEO_PROCESSING_ERROR("POST_014", "Error processing video. Please try again later.");

    private final String code;
    private final String message;

    PostErrorEnums(String code, String message) {
        this.code = code;
        this.message = message;
    }
}

