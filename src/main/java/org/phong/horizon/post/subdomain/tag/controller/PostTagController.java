package org.phong.horizon.post.subdomain.tag.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.phong.horizon.post.subdomain.tag.dto.CreateTagRequest;
import org.phong.horizon.post.subdomain.tag.dto.TagResponse;
import org.phong.horizon.post.subdomain.tag.dto.UpdateTagRequest;
import org.phong.horizon.post.subdomain.tag.service.TagService;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<List<TagResponse>> getAllTags() {
        List<TagResponse> tags = tagService.getAllTags();
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

