package org.phong.horizon.post.controllers;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.phong.horizon.core.responses.RestApiResponse;
import org.phong.horizon.post.services.PostBookmarkService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("api/posts/{postId}/bookmarks")
class PostBookmarkController {
    PostBookmarkService postBookmarkService;
    
    @PostMapping()
    public ResponseEntity<RestApiResponse<Void>> bookmarkPost(@PathVariable UUID postId) {
        postBookmarkService.bookmarkPost(postId);

        return RestApiResponse.created();
    }

    @DeleteMapping()
    public ResponseEntity<RestApiResponse<Void>> deleteBookmark(@PathVariable UUID postId) {
        postBookmarkService.deleteBookmark(postId);

        return RestApiResponse.noContent();
    }

    @GetMapping()
    public ResponseEntity<RestApiResponse<Boolean>> isBookmarked(@PathVariable UUID postId) {
        boolean result = postBookmarkService.isBookmarked(postId);
        return RestApiResponse.success(result);
    }
}
