package org.phong.horizon.post.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.phong.horizon.core.enums.Role;
import org.phong.horizon.core.enums.Visibility;
import org.phong.horizon.core.services.AuthService;
import org.phong.horizon.core.utils.ObjectHelper;
import org.phong.horizon.post.dtos.CreatePostRequest;
import org.phong.horizon.post.dtos.PostCreatedDto;
import org.phong.horizon.post.dtos.PostRespond;
import org.phong.horizon.post.dtos.UpdatePostRequest;
import org.phong.horizon.post.enums.PostErrorEnums;
import org.phong.horizon.post.events.PostCreatedEvent;
import org.phong.horizon.post.events.PostDeletedEvent;
import org.phong.horizon.post.events.PostUpdatedEvent;
import org.phong.horizon.post.exceptions.PostNotFoundException;
import org.phong.horizon.post.exceptions.PostPermissionDenialException;
import org.phong.horizon.post.infrastructure.mapstruct.PostMapper;
import org.phong.horizon.post.infrastructure.persistence.entities.Post;
import org.phong.horizon.post.infrastructure.persistence.repositories.PostRepository;
import org.phong.horizon.storage.infrastructure.persistence.entities.Asset;
import org.phong.horizon.storage.service.StorageService;
import org.phong.horizon.user.infrastructure.persistence.entities.User;
import org.phong.horizon.user.services.UserService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final AuthService authService;
    private final PostMapper postMapper;
    private final StorageService storageService;
    private final UserService userService;
    private final ApplicationEventPublisher eventPublisher;

    @PostAuthorize("hasRole('ADMIN') or " +
            "returnObject.visibility == T(org.phong.horizon.core.enums.Visibility).PUBLIC or " +
//            "(returnObject.visibility != T(org.phong.horizon.infrastructure.enums.Visibility).PRIVATE and @authService.isFriend(returnObject.user.id)) or " +
            "returnObject.user.id == authentication.principal.id")
    @Transactional(readOnly = true)
    public PostRespond getPostById(UUID id) {
        Post post = postRepository.findById(id).orElseThrow(
                () -> new PostNotFoundException(PostErrorEnums.INVALID_POST_ID.getMessage())
        );
        return postMapper.toDto2(post);
    }

    @Transactional(readOnly = true)
    public List<PostRespond> getMeAllPosts() {
        return postRepository.findAllByUser_Id(authService.getUserIdFromContext())
                .stream()
                .map(postMapper::toDto2)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PostRespond> getAllPublicPosts() {
        return postRepository.findAllByVisibility(Visibility.PUBLIC)
                .stream()
                .map(postMapper::toDto2)
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public List<PostRespond> getAllPostsForAdmin() {
        return postRepository.findAll().stream()
                .map(postMapper::toDto2)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PostRespond> getAllPublicPostsByUserId(UUID userId) {
        return postRepository.findAllByUser_IdAndVisibility(userId, Visibility.PUBLIC)
                .stream()
                .map(postMapper::toDto2)
                .collect(Collectors.toList());
    }

    @Transactional
    public PostCreatedDto createPost(CreatePostRequest request) {
        UUID currentUserId = authService.getUserIdFromContext();
        User currentUser = userService.getRefById(currentUserId);

        Asset asset = storageService.getRefById(request.videoAssetId());
        Post post = postMapper.toEntity(request);

        post.setUser(currentUser);
        post.setVideoAsset(asset);
        Post createdPost = postRepository.save(post);

        eventPublisher.publishEvent(new PostCreatedEvent(
                this, createdPost.getId(), currentUserId, post.getCaption(), post.getDescription()
        ));
        log.info("Created new post: {}", createdPost);

        return new PostCreatedDto(createdPost.getId());
    }

    @Transactional
    public void updatePost(UUID id, UpdatePostRequest request) {
        UUID currentUserId = authService.getUserIdFromContext();
        Post originalPost = findPostById(id);
        Post oldPost = postMapper.clonePost(originalPost);
        if (originalPost == null) {
            throw new PostNotFoundException(PostErrorEnums.INVALID_POST_ID.getMessage());
        }

        if (!originalPost.getUser().getId().equals(currentUserId) && !authService.hasRole(Role.ADMIN)) {
            throw new PostPermissionDenialException(PostErrorEnums.UNAUTHORIZED_POST_UPDATE.getMessage());
        }

        Post updatedPost = postMapper.partialUpdate(request, originalPost);

        if (request.videoAssetId() != null) {
            Asset asset = storageService.findAssetById(request.videoAssetId());
            updatedPost.setVideoAsset(asset);
        }

        Post savedPost = postRepository.save(updatedPost);
        log.info("Updated post: {}", savedPost);

        eventPublisher.publishEvent(new PostUpdatedEvent(
                this, savedPost.getId(), currentUserId, savedPost.getCaption(), savedPost.getDescription(),
                ObjectHelper.extractChangesWithCommonsLang(postMapper.toDto1(oldPost), postMapper.toDto1(savedPost))
        ));
    }

    @Transactional
    public void deletePost(UUID id) {
        UUID currentUserId = authService.getUserIdFromContext();
        Post post = findPostById(id);
        if (post == null) {
            throw new PostNotFoundException(PostErrorEnums.INVALID_POST_ID.getMessage());
        }

        if (!post.getUser().getId().equals(currentUserId) && !authService.hasRole(Role.ADMIN)) {
            throw new PostPermissionDenialException(PostErrorEnums.UNAUTHORIZED_POST_DELETE.getMessage());
        }

        postRepository.delete(post);
        log.info("Deleted post: {}", post);

        eventPublisher.publishEvent(new PostDeletedEvent(this, post.getId(), currentUserId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void deleteAllPostsByUser(UUID userId) {
        postRepository.deleteAllByUser_Id(userId);
        log.info("Deleted all posts by admin: {}", userId);
    }

    @Transactional(readOnly = true)
    public Post findPostById(UUID postId) {
        return postRepository.findById(postId).orElseThrow(
                () -> new PostNotFoundException(PostErrorEnums.INVALID_POST_ID.getMessage())
        );
    }

    @Transactional(readOnly = true)
    public Post getRefById(UUID postId) {
        return postRepository.getReferenceById(postId);
    }
}
