package org.phong.horizon.comment.services;

import lombok.extern.slf4j.Slf4j;
import org.phong.horizon.comment.infrastructure.persistence.entities.Comment;
import org.phong.horizon.comment.infrastructure.persistence.entities.CommentMention;
import org.phong.horizon.comment.infrastructure.persistence.repositories.CommentMentionRepository;
import org.phong.horizon.user.infrastructure.persistence.entities.User;
import org.phong.horizon.user.services.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Slf4j
@Service
public class CommentMentionService {
    private static final Pattern pattern = Pattern.compile("@[\\w-]+");
    private final CommentMentionRepository commentMentionRepository;
    private final UserService userService;

    public CommentMentionService(CommentMentionRepository commentMentionRepository,
                                 UserService userService) {
        this.commentMentionRepository = commentMentionRepository;
        this.userService = userService;
    }

    @Transactional
    public void createMentions(Comment comment) {
        List<String> mentionUser = extractMentions(comment.getContent());
        Map<String, User> userMap = userService.getUsersByUsernames(mentionUser);

        if (userMap.isEmpty()) {
            log.warn("No users found with usernames: {}", mentionUser);
            return;
        }

        List<CommentMention> mentions = mentionUser.stream()
                .filter(userMap::containsKey)
                .map(username -> {
                    CommentMention commentMention = new CommentMention();
                    commentMention.setComment(comment);
                    commentMention.setMentionedUser(userMap.get(username));
                    return commentMention;
                })
                .toList();

        commentMentionRepository.saveAll(mentions);
    }

    @Transactional
    public void renewMentions(Comment comment) {
        deleteMentions(comment);
        createMentions(comment);
    }

    protected void deleteMentions(Comment comment) {
        List<CommentMention> mentions = commentMentionRepository.findAllByComment(comment);

        if (mentions.isEmpty()) {
            log.info("No mentions found for comment: {}", comment.getId());
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
