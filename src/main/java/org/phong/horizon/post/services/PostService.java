package org.phong.horizon.post.services;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.phong.horizon.post.enums.PostStatus;
import org.phong.horizon.core.enums.Role;
import org.phong.horizon.core.enums.Visibility;
import org.phong.horizon.core.services.AuthService;
import org.phong.horizon.core.utils.HttpRequestUtils;
import org.phong.horizon.core.utils.ObjectHelper;
import org.phong.horizon.post.dtos.CreatePostRequest;
import org.phong.horizon.post.dtos.PostCreatedDto;
import org.phong.horizon.post.dtos.PostResponse;
import org.phong.horizon.post.dtos.UpdatePostRequest;
import org.phong.horizon.post.enums.PostErrorEnums;
import org.phong.horizon.post.events.PostCreatedEvent;
import org.phong.horizon.post.events.PostDeletedEvent;
import org.phong.horizon.post.events.PostUpdatedEvent;
import org.phong.horizon.post.exceptions.PostNotFoundException;
import org.phong.horizon.post.exceptions.PostPermissionDenialException;
import org.phong.horizon.post.exceptions.PostWithAssetNotFoundException;
import org.phong.horizon.post.infrastructure.mapstruct.PostMapper;
import org.phong.horizon.post.infrastructure.persistence.entities.Post;
import org.phong.horizon.post.infrastructure.persistence.repositories.PostRepository;
import org.phong.horizon.post.subdomain.category.services.PostCategoryService;
import org.phong.horizon.storage.dtos.AssetRespond;
import org.phong.horizon.storage.infrastructure.persistence.entities.Asset;
import org.phong.horizon.storage.service.StorageService;
import org.phong.horizon.user.infrastructure.persistence.entities.User;
import org.phong.horizon.user.services.UserService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
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
    private final PostCategoryService postCategoryService;

    @PostAuthorize("hasRole('ADMIN') or " +
            "returnObject.visibility == T(org.phong.horizon.core.enums.Visibility).PUBLIC or " +
//            "(returnObject.visibility != T(org.phong.horizon.infrastructure.enums.Visibility).PRIVATE and @authService.isFriend(returnObject.user.id)) or " +
            "returnObject.user.id == authentication.principal.id")
    @Transactional(readOnly = true)
    public PostResponse getPostById(UUID id) {
        Post post = postRepository.findById(id).orElseThrow(
                () -> new PostNotFoundException(PostErrorEnums.INVALID_POST_ID.getMessage())
        );
        return postMapper.toDto2(post);
    }

    @Transactional(readOnly = true)
    public List<PostResponse> getMeAllPosts() {
        return postRepository.findAllByUser_Id(authService.getUserIdFromContext())
                .stream()
                .map(postMapper::toDto2)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PostResponse> getAllPublicPosts() {
        return postRepository.findAllByVisibility(Visibility.PUBLIC)
                .stream()
                .map(postMapper::toDto2)
                .collect(Collectors.toList());
    }

    @Transactional
    public Page<PostResponse> getAllPublicPosts(Pageable pageable, UUID excludePostId, String categoryName) {
        if (categoryName != null && !categoryName.isBlank()) {
            if (excludePostId == null) {
                return postRepository.findAllByVisibilityAndCategoryName(
                        Visibility.PUBLIC, categoryName.toUpperCase(), pageable
                ).map(postMapper::toDto2);
            } else {
                return postRepository.findAllByVisibilityAndIdNotAndCategoryName(
                        Visibility.PUBLIC, excludePostId, categoryName.toUpperCase(), pageable
                ).map(postMapper::toDto2);
            }
        } else {
            if (excludePostId == null) {
                return postRepository.findAllByVisibility(
                        Visibility.PUBLIC, pageable
                ).map(postMapper::toDto2);
            } else {
                return postRepository.findAllByVisibilityAndIdNot(
                        Visibility.PUBLIC, excludePostId, pageable
                ).map(postMapper::toDto2);
            }
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public List<PostResponse> getAllPostsForAdmin() {
        return postRepository.findAll().stream()
                .map(postMapper::toDto2)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<PostResponse> getAllPublicPostsByUserId(Pageable pageable, UUID userId, UUID excludePostId) {
        if (excludePostId == null) {
            return postRepository
                    .findAllByUser_IdAndVisibility(pageable, userId, Visibility.PUBLIC)
                    .map(postMapper::toDto2);
        } else {
            return postRepository
                    .findAllByUser_IdAndVisibilityAndIdNot(pageable, userId, Visibility.PUBLIC, excludePostId)
                    .map(postMapper::toDto2);
        }
    }

    public long getCountAllPostByUserId(UUID userId) {
        return postRepository.countAllByUserId(userId);
    }

    @Transactional
    public PostCreatedDto createPost(CreatePostRequest request) {
        AssetRespond assetRespond = storageService.createAsset(request.videoAsset());
        Asset asset;

        try {
            asset = storageService.getRefById(assetRespond.id());
        } catch (EntityNotFoundException e) {
            throw new PostWithAssetNotFoundException(PostErrorEnums.POST_ASSET_NOT_FOUND.getMessage());
        }

        UUID currentUserId = authService.getUserIdFromContext();
        User currentUser = userService.getRefById(currentUserId);

        Post post = postMapper.toEntity(request);

        post.setUser(currentUser);
        post.setVideoAsset(asset);
        post.setCategory(postCategoryService.getRefByName(request.categoryName().toUpperCase()));

        if (request.status() == null) {
            post.setStatus(PostStatus.Draft); // Default to Draft if not provided
        } else {
            post.setStatus(request.status());
        }

        Post createdPost = postRepository.save(post);

        eventPublisher.publishEvent(new PostCreatedEvent(
                this, createdPost.getId(), currentUserId, post.getCaption(), post.getDescription()
        ));
        log.info("Created new post: {}", createdPost);

        return new PostCreatedDto(createdPost.getId(), createdPost.getStatus()); // Ensure status is included
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

        if (request.categoryName() != null) {
            updatedPost.setCategory(postCategoryService.getRefByName(request.categoryName().toUpperCase()));
        }

        if (request.status() != null) { // Allow status update
            updatedPost.setStatus(request.status());
        }

        Post savedPost = postRepository.save(updatedPost);
        log.info("Updated post: {}", savedPost);

        String userAgent = Objects.requireNonNull(HttpRequestUtils.getCurrentHttpRequest()).getHeader("User-Agent");
        String clientIp = HttpRequestUtils.getClientIpAddress(HttpRequestUtils.getCurrentHttpRequest());

        eventPublisher.publishEvent(new PostUpdatedEvent(
                this, savedPost.getId(), currentUserId, savedPost.getCaption(), savedPost.getDescription(),
                ObjectHelper.extractChangesWithCommonsLang(postMapper.toDto1(oldPost), postMapper.toDto1(savedPost)),
                userAgent, clientIp
        ));
    }

    @Transactional
    public void softDeletePostByUserId(UUID id) {
        postRepository.softDeleteAllPostByUserId(id);
    }

    @Transactional
    public void restorePostByUserId(UUID id) {
        postRepository.restoreAllPostByUserId(id);
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
