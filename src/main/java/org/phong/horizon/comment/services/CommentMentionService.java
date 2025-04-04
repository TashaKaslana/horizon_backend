package org.phong.horizon.comment.services;

import lombok.extern.slf4j.Slf4j;
import org.phong.horizon.comment.infrastructure.persistence.entities.Comment;
import org.phong.horizon.comment.infrastructure.persistence.entities.CommentMention;
import org.phong.horizon.comment.infrastructure.persistence.repositories.CommentMentionRepository;
import org.phong.horizon.user.infrastructure.persistence.entities.User;
import org.phong.horizon.user.services.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;

@Slf4j
@Service
public class CommentMentionService {
    private static final Pattern pattern = Pattern.compile("@[\\w-]+");
    private final CommentMentionRepository commentMentionRepository;
    private final UserService userService;
    private final CommentService commentService;

    public CommentMentionService(CommentMentionRepository commentMentionRepository,
                                 UserService userService,
                                 CommentService commentService) {
        this.commentMentionRepository = commentMentionRepository;
        this.userService = userService;
        this.commentService = commentService;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void createMentions(UUID commentId) {
        Comment comment = commentService.findById(commentId);
        if (comment != null) {
            createMentionsInternal(comment);
        } else {
            log.warn("Comment not found in createMentionsForListener: {}", commentId);
        }
    }

    @Transactional
    public void renewMentions(UUID commentId) {
        deleteMentions(commentId);
        Comment comment = commentService.findById(commentId);

        if (comment != null) {
            createMentionsInternal(comment);
        } else {
            log.warn("Comment with ID: {} not found for renewing mentions", commentId);
        }
    }

    private void createMentionsInternal(Comment comment) {
        List<String> mentionUsernames = extractMentions(comment.getContent());
        if (mentionUsernames.isEmpty()) {
            log.debug("No mentions found in content for comment: {}", comment.getId());
            return;
        }

        Map<String, User> userMap = userService.getUsersByUsernames(mentionUsernames);

        if (userMap == null || userMap.isEmpty()) {
            log.warn("No users found matching mentioned usernames: {} for comment: {}", mentionUsernames, comment.getId());
            return;
        }

        List<CommentMention> mentionsToSave = mentionUsernames.stream()
                .map(userMap::get)
                .filter(Objects::nonNull)
                .map(user -> {
                    CommentMention commentMention = new CommentMention();
                    commentMention.setComment(comment);
                    commentMention.setMentionedUser(user);

                    return commentMention;
                })
                .toList();

        if (!mentionsToSave.isEmpty()) {
            commentMentionRepository.saveAll(mentionsToSave);
            log.info("Saved {} new mentions for comment: {}", mentionsToSave.size(), comment.getId());
        } else {
            log.debug("No valid users found for mentions, nothing saved for comment: {}", comment.getId());
        }
    }

    protected void deleteMentions(UUID commentId) {
        List<CommentMention> mentions = commentMentionRepository.findAllByComment_Id(commentId);

        if (mentions.isEmpty()) {
            log.info("No mentions found for comment: {}", commentId);
            return;
        }

        commentMentionRepository.deleteAll(mentions);
    }

    private List<String> extractMentions(String content) {
        if (content == null || content.isEmpty()) {
            return Collections.emptyList();
        }

        return pattern.matcher(content)
                .results()
                .map(match -> match.group().substring(1))
                .distinct()
                .toList();
    }
}
