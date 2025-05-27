package org.phong.horizon.post.subdomain.tag.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.phong.horizon.core.responses.RestApiResponse;
import org.phong.horizon.post.subdomain.tag.dto.CreateTagRequest;
import org.phong.horizon.post.subdomain.tag.dto.TagResponse;
import org.phong.horizon.post.subdomain.tag.dto.UpdateTagRequest;
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
}

