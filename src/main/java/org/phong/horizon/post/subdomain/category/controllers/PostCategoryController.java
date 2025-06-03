package org.phong.horizon.post.subdomain.category.controllers;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.phong.horizon.core.enums.SystemCategory;
import org.phong.horizon.core.responses.RestApiResponse;
import org.phong.horizon.historyactivity.annotations.LogActivity;
import org.phong.horizon.historyactivity.enums.ActivityTypeCode;
import org.phong.horizon.post.subdomain.category.dtos.CreatePostCategoryRequest;
import org.phong.horizon.post.subdomain.category.dtos.PostCategorySummary;
import org.phong.horizon.post.subdomain.category.dtos.PostCategoryWithCountDto;
import org.phong.horizon.post.subdomain.category.dtos.UpdatePostCategoryRequest;
import org.phong.horizon.post.subdomain.category.entities.PostCategory;
import org.phong.horizon.post.subdomain.category.services.PostCategoryService;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/post-categories")
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PostCategoryController {
    PostCategoryService postCategoryService;

    @GetMapping()
    public ResponseEntity<RestApiResponse<List<PostCategory>>> getPostCategories(@ParameterObject Pageable pageable) {
        Page<PostCategory> categories = postCategoryService.getPostCategories(pageable);

        return RestApiResponse.success(categories);
    }

    @GetMapping("/{postCategoryName}")
    public ResponseEntity<RestApiResponse<PostCategorySummary>> getPostCategoryByName(@PathVariable String postCategoryName) {
        PostCategorySummary category = postCategoryService.getPostCategoryByName(postCategoryName);

        return RestApiResponse.success(category);
    }

    @GetMapping("/id/{postCategoryId}")
    public ResponseEntity<RestApiResponse<PostCategorySummary>> getPostCategoryById(@PathVariable UUID postCategoryId) {
        PostCategorySummary category = postCategoryService.getPostCategoryById(postCategoryId);

        return RestApiResponse.success(category);
    }

    @LogActivity(
            activityCode = ActivityTypeCode.POST_CATEGORY_CREATE,
            description = "Create a new post category",
            targetType = SystemCategory.POST,
            targetIdExpression = "#result.body.data.id"
    )
    @PostMapping()
    public ResponseEntity<RestApiResponse<PostCategory>> createPostCategory(@RequestBody CreatePostCategoryRequest postCategory) {
        PostCategory createdCategory = postCategoryService.createPostCategory(postCategory);

        return RestApiResponse.created(createdCategory);
    }

    @LogActivity(
            activityCode = ActivityTypeCode.POST_CATEGORY_UPDATE,
            description = "Update a post category",
            targetType = SystemCategory.POST,
            targetIdExpression = "#postCategoryId"
    )
    @PatchMapping("/{postCategoryId}")
    public ResponseEntity<RestApiResponse<PostCategorySummary>> updatePostCategory(@PathVariable UUID postCategoryId,
                                                                             @RequestBody UpdatePostCategoryRequest postCategory) {
        PostCategorySummary updatedCategory = postCategoryService.updatePostCategory(postCategoryId, postCategory);

        return RestApiResponse.success(updatedCategory);
    }

    @LogActivity(activityCode = ActivityTypeCode.POST_CATEGORY_DELETE,
            description = "Delete a post category",
            targetType = SystemCategory.POST,
            targetIdExpression = "#postCategoryId"
    )
    @DeleteMapping("/{postCategoryId}")
    public ResponseEntity<RestApiResponse<Void>> deletePostCategory(@PathVariable UUID postCategoryId) {
        postCategoryService.deletePostCategory(postCategoryId);
        return RestApiResponse.noContent();
    }

    @GetMapping("/with-counts")
    public ResponseEntity<RestApiResponse<List<PostCategoryWithCountDto>>> getCategoriesWithCounts(@ParameterObject Pageable pageable) {
        Page<PostCategoryWithCountDto> categoriesWithCounts = postCategoryService.getCategoriesWithPostCount(pageable);
        return RestApiResponse.success(categoriesWithCounts);
    }

    @GetMapping("/all-with-counts")
    public ResponseEntity<RestApiResponse<List<PostCategoryWithCountDto>>> getAllCategoriesWithCounts() {
        List<PostCategoryWithCountDto> categoriesWithCounts = postCategoryService.getAllCategoriesWithPostCount();
        return RestApiResponse.success(categoriesWithCounts);
    }

    @GetMapping("/with-counts/id/{postCategoryId}")
    public ResponseEntity<RestApiResponse<PostCategoryWithCountDto>> getCategoryWithCountById(@PathVariable UUID postCategoryId) {
        PostCategoryWithCountDto categoryWithCount = postCategoryService.getCategoryWithCountById(postCategoryId);
        return RestApiResponse.success(categoryWithCount);
    }

    @GetMapping("/with-counts/{postCategoryName}")
    public ResponseEntity<RestApiResponse<PostCategoryWithCountDto>> getCategoryWithCountByName(@PathVariable String postCategoryName) {
        PostCategoryWithCountDto categoryWithCount = postCategoryService.getCategoryWithCountByName(postCategoryName);
        return RestApiResponse.success(categoryWithCount);
    }
}
