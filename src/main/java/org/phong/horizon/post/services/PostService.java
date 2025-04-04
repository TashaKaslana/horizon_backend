package org.phong.horizon.post.services;

import org.phong.horizon.infrastructure.enums.Role;
import org.phong.horizon.infrastructure.enums.Visibility;
import org.phong.horizon.infrastructure.services.AuthService;
import org.phong.horizon.post.dtos.CreatePostRequest;
import org.phong.horizon.post.dtos.PostCreatedDto;
import org.phong.horizon.post.dtos.PostRespond;
import org.phong.horizon.post.dtos.UpdatePostRequest;
import org.phong.horizon.post.enums.PostErrorEnums;
import org.phong.horizon.post.exceptions.PostNotFoundException;
import org.phong.horizon.post.exceptions.PostPermissionDenialException;
import org.phong.horizon.post.infraustructure.mapstruct.PostMapper;
import org.phong.horizon.post.infraustructure.persistence.entities.Post;
import org.phong.horizon.post.infraustructure.persistence.repositories.PostRepository;
import org.phong.horizon.user.infrastructure.persistence.entities.User;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PostService {
    private final PostRepository postRepository;
    private final AuthService authService;
    private final PostMapper postMapper;

    public PostService(PostRepository postRepository,
                       PostMapper postMapper,
                       AuthService authService) {
        this.postRepository = postRepository;
        this.authService = authService;
        this.postMapper = postMapper;
    }

    @PostAuthorize("hasRole('ADMIN') or " +
            "returnObject.visibility == T(org.phong.horizon.infrastructure.enums.Visibility).PUBLIC or " +
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
        User currentUser = authService.getUser();

        Post post = postMapper.toEntity(request);
        post.setUser(currentUser);
        Post createdPost = postRepository.save(post);

        return new PostCreatedDto(createdPost.getId());
    }

    @Transactional
    public void updatePost(UUID id, UpdatePostRequest request) {
        User currentUser = authService.getUser();
        Post post = postRepository.findById(id).orElseThrow(
                () -> new PostNotFoundException(PostErrorEnums.INVALID_POST_ID.getMessage())
        );

        if (!post.getUser().getId().equals(currentUser.getId()) && !authService.hasRole(Role.ADMIN)) {
            throw new PostPermissionDenialException(PostErrorEnums.UNAUTHORIZED_POST_UPDATE.getMessage());
        }

        Post updatedPost = postMapper.partialUpdate(request, post);
        postRepository.save(updatedPost);
    }

    @Transactional
    public void deletePost(UUID id) {
        User currentUser = authService.getUser();
        Post post = postRepository.findById(id).orElseThrow(
                () -> new PostNotFoundException(PostErrorEnums.INVALID_POST_ID.getMessage())
        );

        if (!post.getUser().getId().equals(currentUser.getId()) && !authService.hasRole(Role.ADMIN)) {
            throw new PostPermissionDenialException(PostErrorEnums.UNAUTHORIZED_POST_DELETE.getMessage());
        }

        postRepository.delete(post);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void deleteAllPostsByUser(UUID userId) {
        postRepository.deleteAllByUser_Id(userId);
    }

    @Transactional
    public Post findPostById(UUID postId) {
        return postRepository.findById(postId).orElseThrow(
                () -> new PostNotFoundException(PostErrorEnums.INVALID_POST_ID.getMessage())
        );
    }
}
