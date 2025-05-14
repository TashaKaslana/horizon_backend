package org.phong.horizon.historyactivity.services;

import org.phong.horizon.historyactivity.dtos.ActivityPart;
import org.phong.horizon.historyactivity.dtos.HistoryActivityDto;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class ActivityMessageBuilder {

    public List<ActivityPart> buildMessageParts(HistoryActivityDto dto) {
        String actorName = dto.user() != null ? dto.user().displayName() : "System";
        String code = dto.activityType().getCode();
        Map<String, Object> detail = dto.activityDetail() != null ? dto.activityDetail() : Map.of();
        String targetId = dto.targetId() != null ? dto.targetId().toString() : null;

        return switch (code) {
            case "post_create" -> List.of(
                    ActivityPart.text(actorName + " created "),
                    ActivityPart.object("post", targetId, "a post"),
                    ActivityPart.text(".")
            );
            case "post_like" -> List.of(
                    ActivityPart.text(actorName + " liked "),
                    ActivityPart.object("post", targetId, "a post"),
                    ActivityPart.text(".")
            );
            case "post_update" -> List.of(
                    ActivityPart.text(actorName + " updated "),
                    ActivityPart.object("post", targetId, "a post"),
                    ActivityPart.text(".")
            );
            case "post_delete" -> List.of(
                    ActivityPart.text(actorName + " deleted "),
                    ActivityPart.object("post", targetId, "a post"),
                    ActivityPart.text(".")
            );
            case "comment_create" -> List.of(
                    ActivityPart.text(actorName + " commented on "),
                    ActivityPart.object("post", targetId, "a post"),
                    ActivityPart.text(".")
            );
            case "comment_like" -> List.of(
                    ActivityPart.text(actorName + " liked "),
                    ActivityPart.object("comment", targetId, "a comment"),
                    ActivityPart.text(".")
            );
            case "comment_update" -> List.of(
                    ActivityPart.text(actorName + " updated "),
                    ActivityPart.object("comment", targetId, "a comment"),
                    ActivityPart.text(".")
            );
            case "comment_delete" -> List.of(
                    ActivityPart.text(actorName + " deleted "),
                    ActivityPart.object("comment", targetId, "a comment"),
                    ActivityPart.text(".")
            );
            case "user_follow" -> List.of(
                    ActivityPart.text(actorName + " followed "),
                    ActivityPart.object("user", targetId, "a user"),
                    ActivityPart.text(".")
            );
            case "user_unfollow" -> List.of(
                    ActivityPart.text(actorName + " unfollowed "),
                    ActivityPart.object("user", targetId, "a user"),
                    ActivityPart.text(".")
            );
            case "post_report" -> List.of(
                    ActivityPart.text(actorName + " reported "),
                    ActivityPart.object("post", targetId, "a post"),
                    ActivityPart.text(".")
            );
            case "comment_report" -> List.of(
                    ActivityPart.text(actorName + " reported "),
                    ActivityPart.object("comment", targetId, "a comment"),
                    ActivityPart.text(".")
            );
            case "profile_update" -> List.of(
                    ActivityPart.text(actorName + " updated their profile.")
            );

            // fallback
            default -> List.of(
                    ActivityPart.text(actorName + " performed an action: " + dto.activityType().getName())
            );
        };
    }
}
