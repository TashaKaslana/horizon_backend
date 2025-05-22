package org.phong.horizon.post.subdomain.tag.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.phong.horizon.post.subdomain.tag.dto.CreateTagRequest;
import org.phong.horizon.post.subdomain.tag.dto.TagResponse;
import org.phong.horizon.post.subdomain.tag.dto.UpdateTagRequest;
import org.phong.horizon.post.subdomain.tag.service.TagService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/posts/tags")
@RequiredArgsConstructor
public class PostTagController {

    private final TagService tagService;

    @PostMapping
    public ResponseEntity<TagResponse> createTag(@Valid @RequestBody CreateTagRequest createTagRequest) {
        TagResponse createdTag = tagService.createTag(createTagRequest);
        return new ResponseEntity<>(createdTag, HttpStatus.CREATED);
    }

    @GetMapping("/{tagId}")
    public ResponseEntity<TagResponse> getTagById(@PathVariable UUID tagId) {
        TagResponse tagResponse = tagService.getTagById(tagId);
        return ResponseEntity.ok(tagResponse);
    }

    @GetMapping
    public ResponseEntity<Page<TagResponse>> getAllTags(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String slug,
            Pageable pageable) {
        Page<TagResponse> tags = tagService.getAllTags(name, slug, pageable);
        return ResponseEntity.ok(tags);
    }

    @PutMapping("/{tagId}")
    public ResponseEntity<TagResponse> updateTag(
            @PathVariable UUID tagId,
            @Valid @RequestBody UpdateTagRequest updateTagRequest) {
        TagResponse updatedTag = tagService.updateTag(tagId, updateTagRequest);
        return ResponseEntity.ok(updatedTag);
    }
}

