package org.phong.horizon.post.subdomain.tag.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.phong.horizon.post.infrastructure.persistence.entities.Post;
import org.phong.horizon.post.infrastructure.persistence.repositories.PostRepository;
import org.phong.horizon.post.subdomain.tag.entity.PostTag;
import org.phong.horizon.post.subdomain.tag.entity.Tag;
import org.phong.horizon.post.subdomain.tag.repository.PostTagRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Service to manage the relationship between Post and Tag entities using the PostTag pivot entity
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PostTagService {
    private final PostTagRepository postTagRepository;
    private final TagService tagService;
    private final PostRepository postRepository;

    /**
     * Get all tag names associated with a post
     *
     * @param postId The post ID
     * @return List of tag names
     */
    @Transactional(readOnly = true)
    public List<String> getTagNamesByPostId(UUID postId) {
        return postTagRepository.findTagNamesByPostId(postId);
    }

    /**
     * Get tag names by post ID - Method specifically used by MapStruct for DTO mapping
     *
     * @param postId The post ID
     * @return List of tag names
     */
    @org.mapstruct.Named("getTagNames")
    @Transactional(readOnly = true)
    public List<String> getTagNames(UUID postId) {
        return getTagNamesByPostId(postId);
    }

    /**
     * Get all tags associated with a post
     *
     * @param postId The post ID
     * @return List of Tag entities
     */
    @Transactional(readOnly = true)
    public List<Tag> getTagsByPostId(UUID postId) {
        return postTagRepository.findTagsByPostId(postId);
    }

    /**
     * Update tags for a post by deleting existing associations and creating new ones
     *
     * @param post The post entity
     * @param tagNames List of tag names to associate with the post
     */
    @Transactional
    public void updatePostTags(Post post, List<String> tagNames) {
        // Remove existing tags
        postTagRepository.deleteByPostId(post.getId());

        // Return if no tags to add
        if (tagNames == null || tagNames.isEmpty()) {
            return;
        }

        // Create new tag associations
        addPostTags(post, tagNames);
    }

    /**
     * Add tags to a post without removing existing ones
     *
     * @param post The post entity
     * @param tagNames List of tag names to associate with the post
     */
    @Transactional
    public void addPostTags(Post post, List<String> tagNames) {
        if (tagNames == null || tagNames.isEmpty()) {
            return;
        }

        List<PostTag> postTags = new ArrayList<>();
        Instant now = Instant.now();

        for (String tagName : tagNames) {
            // Get or create tag
            Tag tag = tagService.getOrCreateTag(tagName);

            // Check if association already exists
            if (!postTagRepository.existsByPostIdAndTagId(post.getId(), tag.getId())) {
                PostTag postTag = new PostTag();
                postTag.setPost(post);
                postTag.setTag(tag);
                postTag.setCreatedAt(now);
                postTag.setUpdatedAt(now);
                postTags.add(postTag);
            }
        }

        // Save all new associations
        if (!postTags.isEmpty()) {
            postTagRepository.saveAll(postTags);
            log.info("Added {} tags to post {}", postTags.size(), post.getId());
        }
    }

    /**
     * Remove a tag from a post
     *
     * @param postId The post ID
     * @param tagId The tag ID
     */
    @Transactional
    public void removeTagFromPost(UUID postId, UUID tagId) {
        List<PostTag> postTags = postTagRepository.findByPostId(postId);
        for (PostTag postTag : postTags) {
            if (postTag.getTag().getId().equals(tagId)) {
                postTagRepository.delete(postTag);
                log.info("Removed tag {} from post {}", tagId, postId);
                return;
            }
        }
    }

    /**
     * Add tags to a post - Takes postId directly
     *
     * @param postId The post ID
     * @param tagNames List of tag names to associate with the post
     */
    @Transactional
    public void addTagsToPost(UUID postId, List<String> tagNames) {
        Post post = postRepository.getReferenceById(postId);
        addPostTags(post, tagNames);
    }

    /**
     * Update tags for a post by deleting existing associations and creating new ones
     *
     * @param postId The post ID
     * @param tagNames List of tag names to associate with the post
     */
    @Transactional
    public void updatePostTags(UUID postId, List<String> tagNames) {
        Post post = postRepository.getReferenceById(postId);
        updatePostTags(post, tagNames);
    }
}
