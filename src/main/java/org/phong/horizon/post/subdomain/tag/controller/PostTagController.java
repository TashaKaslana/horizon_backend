package org.phong.horizon.post.subdomain.tag.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.phong.horizon.core.responses.RestApiResponse;
import org.phong.horizon.post.subdomain.tag.dto.BulkTagDeleteRequest;
import org.phong.horizon.post.subdomain.tag.dto.CreateTagRequest;
import org.phong.horizon.post.subdomain.tag.dto.TagResponse;
import org.phong.horizon.post.subdomain.tag.dto.TagWithCountDto;
import org.phong.horizon.post.subdomain.tag.dto.UpdateTagRequest;
import org.phong.horizon.post.subdomain.tag.entity.Tag;
import org.phong.horizon.post.subdomain.tag.service.PostTagService;
import org.phong.horizon.post.subdomain.tag.service.TagService;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/posts/tags")
@RequiredArgsConstructor
public class PostTagController {

    private final TagService tagService;
    private final PostTagService postTagService;

    @PostMapping
    public ResponseEntity<RestApiResponse<TagResponse>> createTag(@Valid @RequestBody CreateTagRequest createTagRequest) {
        TagResponse createdTag = tagService.createTag(createTagRequest);
        return RestApiResponse.created(createdTag);
    }

    @GetMapping("/{tagId}")
    public ResponseEntity<RestApiResponse<TagResponse>> getTagById(@PathVariable UUID tagId) {
        TagResponse tagResponse = tagService.getTagById(tagId);
        return RestApiResponse.success(tagResponse);
    }

    @GetMapping
    public ResponseEntity<RestApiResponse<List<TagResponse>>> getAllTags(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String slug,
            @ParameterObject Pageable pageable) {
        Page<TagResponse> tags = tagService.getAllTags(name, slug, pageable);
        return RestApiResponse.success(tags);
    }

    @PutMapping("/{tagId}")
    public ResponseEntity<RestApiResponse<TagResponse>> updateTag(
            @PathVariable UUID tagId,
            @Valid @RequestBody UpdateTagRequest updateTagRequest) {
        TagResponse updatedTag = tagService.updateTag(tagId, updateTagRequest);
        return RestApiResponse.success(updatedTag);
    }

    @DeleteMapping("/{tagId}")
    public ResponseEntity<RestApiResponse<Void>> deleteTag(@PathVariable UUID tagId) {
        tagService.deleteTag(tagId);
        return RestApiResponse.noContent();
    }

    /**
     * Get all tags with their post counts
     */
    @GetMapping("/with-counts")
    public ResponseEntity<RestApiResponse<List<TagWithCountDto>>> getTagsWithCounts(@ParameterObject Pageable pageable) {
        Page<TagWithCountDto> tagsWithCounts = tagService.getTagsWithPostCount(pageable);
        return RestApiResponse.success(tagsWithCounts);
    }

    /**
     * Get all tags with their post counts (non-paginated)
     */
    @GetMapping("/all-with-counts")
    public ResponseEntity<RestApiResponse<List<TagWithCountDto>>> getAllTagsWithCounts() {
        List<TagWithCountDto> tagsWithCounts = tagService.getAllTagsWithPostCount();
        return RestApiResponse.success(tagsWithCounts);
    }

    /**
     * Get a specific tag with post count by ID
     */
    @GetMapping("/with-counts/{tagId}")
    public ResponseEntity<RestApiResponse<TagWithCountDto>> getTagWithCountById(@PathVariable UUID tagId) {
        TagWithCountDto tagWithCount = tagService.getTagWithCountById(tagId);
        return RestApiResponse.success(tagWithCount);
    }

    /**
     * Get a specific tag with post count by slug
     */
    @GetMapping("/with-counts/slug/{slug}")
    public ResponseEntity<RestApiResponse<TagWithCountDto>> getTagWithCountBySlug(@PathVariable String slug) {
        TagWithCountDto tagWithCount = tagService.getTagWithCountBySlug(slug);
        return RestApiResponse.success(tagWithCount);
    }

    /**
     * Get a specific tag with post count by name
     */
    @GetMapping("/with-counts/name/{name}")
    public ResponseEntity<RestApiResponse<TagWithCountDto>> getTagWithCountByName(@PathVariable String name) {
        TagWithCountDto tagWithCount = tagService.getTagWithCountByName(name);
        return RestApiResponse.success(tagWithCount);
    }

    /**
     * Get all tags for a specific post
     */
    @GetMapping("/by-post/{postId}")
    public ResponseEntity<RestApiResponse<List<TagResponse>>> getTagsByPostId(@PathVariable UUID postId) {
        List<Tag> tags = postTagService.getTagsByPostId(postId);
        List<TagResponse> tagResponses = tags.stream()
                .map(tagService::convertToResponse)
                .toList();
        return RestApiResponse.success(tagResponses);
    }

    /**
     * Get all tag names for a specific post
     */
    @GetMapping("/names/by-post/{postId}")
    public ResponseEntity<RestApiResponse<List<String>>> getTagNamesByPostId(@PathVariable UUID postId) {
        List<String> tagNames = postTagService.getTagNamesByPostId(postId);
        return RestApiResponse.success(tagNames);
    }

    /**
     * Add tags to a post
     */
    @PostMapping("/post/{postId}/add")
    public ResponseEntity<RestApiResponse<Void>> addTagsToPost(
            @PathVariable UUID postId,
            @RequestBody List<String> tagNames) {
        postTagService.addTagsToPost(postId, tagNames);
        return RestApiResponse.noContent();
    }

    /**
     * Update the tags for a post (replace all existing tags)
     */
    @PutMapping("/post/{postId}")
    public ResponseEntity<RestApiResponse<Void>> updatePostTags(
            @PathVariable UUID postId,
            @RequestBody List<String> tagNames) {
        postTagService.updatePostTags(postId, tagNames);
        return RestApiResponse.noContent();
    }

    /**
     * Remove a tag from a post
     */
    @DeleteMapping("/post/{postId}/tag/{tagId}")
    public ResponseEntity<RestApiResponse<Void>> removeTagFromPost(
            @PathVariable UUID postId,
            @PathVariable UUID tagId) {
        postTagService.removeTagFromPost(postId, tagId);
        return RestApiResponse.noContent();
    }

    /**
     * Remove all tags from a post
     */
    @DeleteMapping("/post/{postId}")
    public ResponseEntity<RestApiResponse<Void>> removeAllTagsFromPost(
            @PathVariable UUID postId) {
        postTagService.updatePostTags(postId, List.of());
        return RestApiResponse.noContent();
    }

    /**
     * Bulk delete multiple tags by their IDs
     */
    @DeleteMapping("/bulk-delete")
    public ResponseEntity<RestApiResponse<Void>> bulkDeleteTags(@Valid @RequestBody BulkTagDeleteRequest request) {
        tagService.bulkDeleteTags(request);
        return RestApiResponse.noContent();
    }
}
