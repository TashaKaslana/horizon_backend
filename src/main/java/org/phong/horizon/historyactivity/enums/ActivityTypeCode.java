package org.phong.horizon.historyactivity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.phong.horizon.historyactivity.exceptions.ActivityTypeNotFoundException;

@Getter
@RequiredArgsConstructor
public enum ActivityTypeCode {

    // Account Category
    USER_CREATE("user_create"),
    USER_LOGIN("user_login"),
    USER_LOGOUT("user_logout"),
    USER_UPDATE("user_update"),
    USER_DELETE("user_delete"),
    USER_RESTORE("user_restore"),
    PASSWORD_CHANGE("password_change"),
    PROFILE_UPDATE("profile_update"),
    EMAIL_CHANGE("email_change"),
    ACCOUNT_DELETION_REQUEST("account_deletion_request"),
    ACCOUNT_RECOVERY_ATTEMPT("account_recovery_attempt"),

    // Post Category
    POST_CREATE("post_create"),
    POST_UPDATE("post_update"),
    POST_DELETE("post_delete"),

    // Interaction Category
    POST_VIEW("post_view"),
    POST_LIKE("post_like"),
    POST_UNLIKE("post_unlike"),
    COMMENT_CREATE("comment_create"),
    COMMENT_DELETE("comment_delete"),
    COMMENT_INTERACTION_CREATE("comment_interaction_create"),
    COMMENT_INTERACTION_DELETE("comment_interaction_delete"),
    USER_FOLLOW("user_follow"),
    USER_UNFOLLOW("user_unfollow"),

    //ASSET Category
    ASSET_CREATE("asset_create"),
    ASSET_UPDATE("asset_update"),
    ASSET_DELETE("asset_delete"),
    ASSET_SIGNATURE("asset_signature"),

    // Moderation Category
    POST_REPORT("post_report"),
    USER_REPORT("user_report"),
    CONTENT_MODERATION_ACTION("content_moderation_action"),

    // Security Category
    SUSPICIOUS_ACTIVITY_ALERT("suspicious_activity_alert");

    private final String code;

    public static ActivityTypeCode fromCode(String code) {
        for (ActivityTypeCode type : ActivityTypeCode.values()) {
            if (type.code.equalsIgnoreCase(code)) {
                return type;
            }
        }

        throw new ActivityTypeNotFoundException("Unknown activity type code: " + code);
    }
}