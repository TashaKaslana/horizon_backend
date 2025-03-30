package org.phong.horizon.post.services;

import org.phong.horizon.infrastructure.enums.RoleEnums;
import org.phong.horizon.infrastructure.services.AuthService;
import org.phong.horizon.post.dtos.CreatePostRequest;
import org.phong.horizon.post.dtos.PostRespond;
import org.phong.horizon.post.dtos.UpdatePostRequest;
import org.phong.horizon.post.enums.PostErrorEnums;
import org.phong.horizon.post.exceptions.PostNotFoundException;
import org.phong.horizon.post.exceptions.PostPermissionDenialException;
import org.phong.horizon.post.infraustructure.mapstruct.PostMapper;
import org.phong.horizon.post.infraustructure.persistence.entities.Post;
import org.phong.horizon.post.infraustructure.persistence.repositories.PostRepository;
import org.phong.horizon.user.infrastructure.persistence.entities.User;
import org.springframework.stereotype.Service;

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

    public PostRespond getPostById(UUID id) {
        Post post = findById(id);

        if (post.getVisibility().equals("PRIVATE") && isOwnershipOrAdmin(post)) {
            throw new PostPermissionDenialException(PostErrorEnums.UNAUTHORIZED_POST_ACCESS.getMessage());
        }

        return postMapper.toDto2(findById(id));
    }

    public List<PostRespond> getMeAllPost() {
        return findAllPostByUserId(authService.getUserId());
    }

    public List<PostRespond> getAllPublicPosts() {
        return postRepository.findAllByVisibility("PUBLIC").stream()
                .map(postMapper::toDto2)
                .collect(Collectors.toList());
    }

    public List<PostRespond> getAll() {
        return postRepository.findAll().stream()
                .map(postMapper::toDto2)
                .collect(Collectors.toList());
    }

    public List<PostRespond> getAllPublicPostsByUserId(UUID userId) {
        return postRepository.findAllByUser_IdAndVisibility(userId, "PUBLIC").stream()
                .map(postMapper::toDto2)
                .collect(Collectors.toList());
    }

    public List<PostRespond> findAllPostByUserId(UUID userId) {
        return postRepository.findAllByUser_Id(userId).stream()
                .map(postMapper::toDto2)
                .collect(Collectors.toList());
    }

    public UUID createPost(CreatePostRequest request) {
        User user = authService.getUser();

        Post post = postMapper.toEntity(request);
        post.setUser(user);

        Post createdPost = postRepository.save(post);

        return createdPost.getId();
    }

    public void updatePost(UUID id, UpdatePostRequest request) {
        Post post = findById(id);
        checkPostOwnership(post);

        Post updatedPost = postMapper.partialUpdate(request, post);
        postRepository.save(updatedPost);
    }

    public void deletePost(UUID id) {
        Post post = findById(id);

        postRepository.delete(post);
    }

    public void deleteMePost(UUID id) {
        Post post = findById(id);

        if (post.getUser().getId().equals(authService.getUserId())) {
            throw new PostPermissionDenialException(PostErrorEnums.UNAUTHORIZED_POST_DELETE.getMessage());
        }

        postRepository.delete(post);
    }

    public void deleteAll(UUID userId) {
        postRepository.deleteAllByUser_Id(userId);
    }

    private void checkPostOwnership(Post post) {
        User user = authService.getUser();
        if (!post.getUser().getId().equals(user.getId())) {
            throw new PostPermissionDenialException(PostErrorEnums.UNAUTHORIZED_POST_UPDATE.getMessage());
        }
    }

    private boolean isOwnershipOrAdmin(Post post) {
        UUID userId = authService.getUserId();
        return post.getUser().getId().equals(userId) || authService.hasRole(RoleEnums.ADMIN.getRole());
    }

    private Post findById(UUID postId) {
        return postRepository.findById(postId).orElseThrow(
                () -> new PostNotFoundException(PostErrorEnums.INVALID_POST_ID.getMessage())
        );
    }
}
