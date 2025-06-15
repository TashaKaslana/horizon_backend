package org.phong.horizon.post.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.phong.horizon.core.enums.InteractionType;
import org.phong.horizon.core.services.AuthService;
import org.phong.horizon.post.dtos.CreatePostInteraction;
import org.phong.horizon.post.dtos.PostInteractionResponse;
import org.phong.horizon.post.enums.PostInteractionError;
import org.phong.horizon.post.exceptions.PostInteractionAlreadyExistException;
import org.phong.horizon.post.exceptions.PostInteractionNotFoundException;
import org.phong.horizon.post.infrastructure.mapstruct.PostInteractionMapper;
import org.phong.horizon.post.events.PostInteractionCreatedEvent;
import org.phong.horizon.post.events.PostInteractionDeletedEvent;
import org.phong.horizon.post.infrastructure.persistence.entities.Post;
import org.phong.horizon.post.infrastructure.persistence.entities.PostInteraction;
import org.phong.horizon.post.infrastructure.persistence.repositories.PostInteractionRepository;
import org.phong.horizon.user.infrastructure.persistence.entities.User;
import org.phong.horizon.user.services.UserService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class PostInteractionService {
    private final AuthService authService;
    private final PostService postService;
    private final UserService userService;
    private final PostInteractionRepository postInteractionRepository;
    private final PostInteractionMapper postInteractionMapper;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public void createInteraction(UUID postId, CreatePostInteraction postInteraction) {
        Post post = postService.getRefById(postId);
        UUID currentUserId = authService.getUserIdFromContext();
        User currentUser = userService.getRefById(currentUserId);

        if (postInteractionRepository.existsByUser_IdAndPost_IdAndInteraction(currentUserId, post.getId(), postInteraction.interactionType())) {
            throw new PostInteractionAlreadyExistException(PostInteractionError.POST_INTERACTION_EXISTS.getMessage());
        }

        PostInteraction interaction = new PostInteraction();
        interaction.setPost(post);
        interaction.setUser(currentUser);
        interaction.setInteraction(postInteraction.interactionType());

        postInteractionRepository.save(interaction);
        eventPublisher.publishEvent(new PostInteractionCreatedEvent(this,
                post.getId(), currentUserId, postInteraction.interactionType()));
    }

    @Transactional(readOnly = true)
    public long getCountInteractionByPostId(UUID postId) {
        return postInteractionRepository.countAllByPost_Id(postId);
    }

    @Transactional
    public void removeInteraction(UUID postId, InteractionType interactionType) {
        UUID currentUserId = authService.getUserIdFromContext();

        PostInteraction interaction = postInteractionRepository.findByPost_IdAndUser_IdAndInteraction(postId, currentUserId, interactionType).orElseThrow(
                () -> new PostInteractionNotFoundException(PostInteractionError.POST_INTERACTION_NOT_FOUND.getMessage())
        );

        postInteractionRepository.delete(interaction);
        eventPublisher.publishEvent(new PostInteractionDeletedEvent(this,
                postId, currentUserId, interactionType));
    }

    @Transactional(readOnly = true)
    public List<PostInteractionResponse> getInteractionsByPostId(UUID postId) {
        List<PostInteraction> interactions = postInteractionRepository.findAllByPost_Id(postId);

        return interactions.stream().map(postInteractionMapper::toDto).collect(Collectors.toList());
    }

    public boolean hasUserInteracted(UUID postId, InteractionType interactionType) {
        UUID currentUserId = authService.getUserIdFromContext();
        return postInteractionRepository.existsByPost_IdAndUser_IdAndInteraction(postId, currentUserId, interactionType);
    }

    //me interact
    public List<UUID> getPostIdsMeInteractedByPostIds(List<UUID> postIds) {
        UUID currentUserId = authService.getUserIdFromContext();

        return postInteractionRepository.findAllByPost_IdsInAndUser_Id(postIds, currentUserId);
    }

    @Transactional
    public void deleteInteractionsByUserId(UUID userId) {
        postInteractionRepository.deleteAllByUser_Id(userId);
    }

    public Map<UUID, Long> getCountInteractionByPostIds(List<UUID> idList) {
        List<Object[]> views = postInteractionRepository.countPostInteractionByPostIds(idList);

        Map<UUID, Long> viewMap = new HashMap<>();

        for (Object[] view : views) {
            viewMap.put((UUID) view[0], (Long) view[1]);
        }

        return viewMap;
    }
}
