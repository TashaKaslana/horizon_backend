package org.phong.horizon.post.controllers;

import org.phong.horizon.core.enums.SystemCategory;
import org.phong.horizon.historyactivity.annotations.LogActivity;
import org.phong.horizon.historyactivity.enums.ActivityTypeCode;
import org.phong.horizon.post.dtos.BulkPostDeleteRequest;
import org.phong.horizon.post.dtos.BulkPostUpdateRequest;
import org.phong.horizon.post.dtos.PostAdminViewDto;
import org.phong.horizon.post.dtos.PostResponse;
import org.phong.horizon.post.services.PostService;
import org.phong.horizon.core.responses.RestApiResponse;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/posts")
public class AdminPostController {

    private final PostService postService;

    public AdminPostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    public ResponseEntity<RestApiResponse<List<PostResponse>>> getAllPostsForAdmin(@ParameterObject Pageable pageable) {
        return RestApiResponse.success(postService.getAllPostsForAdmin(pageable));
    }

    @GetMapping("/all/aggregate")
    public ResponseEntity<RestApiResponse<List<PostAdminViewDto>>> getAllPostWithDetailsForAdmin(@ParameterObject Pageable pageable) {
        return RestApiResponse.success(postService.getPostsForAdmin(pageable));
    }

    @GetMapping("/{postId}")
    public ResponseEntity<RestApiResponse<PostAdminViewDto>> getPostByIdForAdmin(@PathVariable UUID postId) {
        return RestApiResponse.success(postService.getPostByIdForAdmin(postId));
    }

    @DeleteMapping("/{postId}")
    @LogActivity(
            activityCode = ActivityTypeCode.POST_DELETE,
            description = "Admin delete a post",
            targetType = SystemCategory.POST,
            targetIdExpression = "#postId"
    )
    public ResponseEntity<RestApiResponse<Void>> deletePost(@PathVariable UUID postId) {
        postService.deletePost(postId);
        return RestApiResponse.noContent();
    }

    @DeleteMapping("/user/{userId}")
    @LogActivity(
            activityCode = ActivityTypeCode.POST_DELETE,
            description = "Admin delete all posts to user",
            targetType = SystemCategory.USER,
            targetIdExpression = "#userId"
    )
    public ResponseEntity<RestApiResponse<Void>> deleteAllPostsByUser(@PathVariable UUID userId) {
        postService.deleteAllPostsByUser(userId);
        return RestApiResponse.noContent();
    }

    @DeleteMapping("/bulk")
    @LogActivity(
            activityCode = ActivityTypeCode.POST_DELETE,
            description = "Admin bulk delete posts",
            targetType = SystemCategory.POST
    )
    public ResponseEntity<RestApiResponse<Void>> bulkDeletePosts(@RequestBody BulkPostDeleteRequest request) {
        postService.bulkDeletePosts(request);
        return RestApiResponse.noContent();
    }

    @PutMapping("/bulk")
    @LogActivity(
            activityCode = ActivityTypeCode.POST_UPDATE,
            description = "Admin bulk update posts",
            targetType = SystemCategory.POST
    )
    public ResponseEntity<RestApiResponse<List<PostResponse>>> bulkUpdatePosts(@RequestBody BulkPostUpdateRequest request) {
        return RestApiResponse.success(postService.bulkUpdatePosts(request));
    }
}
