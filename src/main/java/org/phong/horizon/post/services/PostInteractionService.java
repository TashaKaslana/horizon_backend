package org.phong.horizon.post.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.phong.horizon.core.enums.InteractionType;
import org.phong.horizon.core.services.AuthService;
import org.phong.horizon.post.dtos.CreatePostInteraction;
import org.phong.horizon.post.dtos.PostInteractionRespond;
import org.phong.horizon.post.enums.PostInteractionError;
import org.phong.horizon.post.exceptions.PostInteractionAlreadyExistException;
import org.phong.horizon.post.exceptions.PostInteractionNotFoundException;
import org.phong.horizon.post.infrastructure.mapstruct.PostInteractionMapper;
import org.phong.horizon.post.infrastructure.persistence.entities.Post;
import org.phong.horizon.post.infrastructure.persistence.entities.PostInteraction;
import org.phong.horizon.post.infrastructure.persistence.repositories.PostInteractionRepository;
import org.phong.horizon.user.infrastructure.persistence.entities.User;
import org.phong.horizon.user.services.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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
    }

    @Transactional(readOnly = true)
    public List<PostInteractionRespond> getInteractionsByPostId(UUID postId) {
        List<PostInteraction> interactions = postInteractionRepository.findAllByPost_Id(postId);

        return interactions.stream().map(postInteractionMapper::toDto).collect(Collectors.toList());
    }

    public boolean hasUserInteracted(UUID postId, InteractionType interactionType) {
        UUID currentUserId = authService.getUserIdFromContext();
        return postInteractionRepository.existsByPost_IdAndUser_IdAndInteraction(postId, currentUserId, interactionType);
    }

    @Transactional
    public void deleteInteractionsByUserId(UUID userId) {
        postInteractionRepository.deleteAllByUser_Id(userId);
    }
}
