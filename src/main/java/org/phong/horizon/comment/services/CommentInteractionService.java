package org.phong.horizon.comment.services;

import lombok.extern.slf4j.Slf4j;
import org.phong.horizon.comment.dtos.CommentInteractionRespond;
import org.phong.horizon.comment.dtos.CreateCommentInteraction;
import org.phong.horizon.comment.enums.CommentInteractionError;
import org.phong.horizon.core.enums.InteractionType;
import org.phong.horizon.comment.exceptions.CommentInteractionExistException;
import org.phong.horizon.comment.exceptions.CommentInteractionNotFoundException;
import org.phong.horizon.comment.infrastructure.mapstruct.CommentInteractionMapper;
import org.phong.horizon.comment.infrastructure.persistence.entities.Comment;
import org.phong.horizon.comment.infrastructure.persistence.entities.CommentInteraction;
import org.phong.horizon.comment.infrastructure.persistence.repositories.CommentInteractionRepository;
import org.phong.horizon.core.services.AuthService;
import org.phong.horizon.comment.events.CommentInteractionCreated;
import org.phong.horizon.comment.events.CommentInteractionDeleted;
import org.phong.horizon.user.infrastructure.persistence.entities.User;
import org.phong.horizon.user.services.UserService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CommentInteractionService {
    private final CommentService commentService;
    private final CommentInteractionRepository repository;
    private final AuthService authService;
    private final CommentInteractionMapper mapper;
    private final UserService userService;
    private final ApplicationEventPublisher eventPublisher;

    public CommentInteractionService(CommentService commentService,
                                     CommentInteractionRepository repository,
                                     AuthService authService,
                                     CommentInteractionMapper commentInteractionMapper,
                                     UserService userService,
                                     ApplicationEventPublisher eventPublisher) {
        this.commentService = commentService;
        this.repository = repository;
        this.authService = authService;
        this.mapper = commentInteractionMapper;
        this.userService = userService;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public CommentInteractionRespond createInteraction(UUID commentId, CreateCommentInteraction request) {
        UUID currentUserId = authService.getUserIdFromContext();
        User currentUser = userService.getRefById(currentUserId);
        Comment comment = commentService.getRefById(commentId);

        boolean exists = repository.existsByComment_IdAndUser_IdAndInteractionType(
                commentId, currentUserId, request.interactionType());
        if (exists) {
            log.warn("Interaction already exists for comment {}, user {}, type {}. Skipping creation.",
                    commentId, currentUserId, request.interactionType());

            throw new CommentInteractionExistException(CommentInteractionError.COMMENT_INTERACTION_EXISTS.getMessage());
        }

        CommentInteraction interaction = new CommentInteraction();
        interaction.setUser(currentUser);
        interaction.setComment(comment);
        interaction.setInteractionType(request.interactionType());

        CommentInteraction created = repository.save(interaction);
        log.debug("Created interaction for comment {}, user {}, type {}",
                commentId, currentUser.getId(), request.interactionType());
        eventPublisher.publishEvent(new CommentInteractionCreated(this,
                comment.getId(), comment.getPost().getId(), currentUserId, request.interactionType()));
        return mapper.toDto2(created);
    }

    @Transactional
    public void deleteInteraction(UUID commentId, InteractionType interactionType) {
        UUID currentUserId = authService.getUserIdFromContext();

        int deletedCount = repository.deleteByComment_IdAndUser_IdAndInteractionType(
                commentId,
                currentUserId,
                interactionType
        );

        if (deletedCount == 0) {
            log.warn("No interaction found to delete for comment {}, user {}, type {}",
                    commentId, currentUserId, interactionType);

            throw new CommentInteractionNotFoundException(
                    CommentInteractionError.COMMENT_INTERACTION_NOT_FOUND.getMessage()
            );
        } else {
            log.info("Deleted interaction for comment {}, user {}, type {}",
                    commentId, currentUserId, interactionType);
            eventPublisher.publishEvent(new CommentInteractionDeleted(this,
                    commentId, commentService.getRefById(commentId).getPost().getId(),
                    currentUserId, interactionType));
        }
    }

    @Transactional(readOnly = true)
    public List<CommentInteractionRespond> getInteractionsByCommentId(UUID commentId) {
        List<CommentInteraction> list = repository.findAllByComment_Id(commentId);

        return list.stream().map(mapper::toDto2).collect(Collectors.toList());
    }


    @Transactional
    public void deleteInteractionsByUserId(UUID userId) {
        repository.deleteAllByUser_Id(userId);
    }
}