package org.phong.horizon.post.subdomain.category.services;

import com.github.slugify.Slugify;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.phong.horizon.core.services.AuthService;
import org.phong.horizon.core.utils.HttpRequestUtils;
import org.phong.horizon.core.utils.ObjectHelper;
import org.phong.horizon.post.enums.PostErrorEnums;
import org.phong.horizon.post.infrastructure.persistence.repositories.PostRepository;
import org.phong.horizon.post.subdomain.category.dtos.BulkPostCategoryDeleteRequest;
import org.phong.horizon.post.subdomain.category.dtos.CreatePostCategoryRequest;
import org.phong.horizon.post.subdomain.category.dtos.PostCategorySummary;
import org.phong.horizon.post.subdomain.category.dtos.PostCategoryWithCountDto;
import org.phong.horizon.post.subdomain.category.dtos.UpdatePostCategoryRequest;
import org.phong.horizon.post.subdomain.category.entities.PostCategory;
import org.phong.horizon.post.subdomain.category.events.PostCategoryUpdate;
import org.phong.horizon.post.subdomain.category.exceptions.PostCategoryExistsException;
import org.phong.horizon.post.subdomain.category.exceptions.PostCategoryNotFoundException;
import org.phong.horizon.post.subdomain.category.mapstruct.PostCategoryMapper;
import org.phong.horizon.post.subdomain.category.repositories.PostCategoryRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PostCategoryService {
    PostCategoryRepository postCategoryRepository;
    PostRepository postRepository;
    PostCategoryMapper mapper;
    ApplicationEventPublisher eventPublisher;
    AuthService authService;

    public Page<PostCategory> getPostCategories(Pageable pageable) {
        return postCategoryRepository.findAll(pageable);
    }

    public PostCategorySummary getPostCategoryByName(String name) {
        return mapper.toDto(getPostCategoryEntityByName(name));
    }

    public PostCategorySummary getPostCategoryById(UUID id) {
        return mapper.toDto(getPostCategoryEntityById(id));
    }

    public PostCategory getPostCategoryEntityByName(String name) {
        return postCategoryRepository.findByName(name).orElseThrow(
                () -> new PostCategoryNotFoundException(PostErrorEnums.POST_CATEGORY_NOT_FOUND.getMessage())
        );
    }

    public PostCategory getPostCategoryEntityById(UUID postCategoryId) {
        return postCategoryRepository.findById(postCategoryId).orElseThrow(
                () -> new PostCategoryNotFoundException(PostErrorEnums.POST_CATEGORY_NOT_FOUND.getMessage())
        );
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public PostCategory createPostCategory(CreatePostCategoryRequest request) {
        boolean isExist = postCategoryRepository.existsByName(request.name());

        if (isExist) {
            throw new PostCategoryExistsException(PostErrorEnums.POST_CATEGORY_EXISTS.getMessage());
        }

        Slugify slugify = Slugify.builder().build();
        String baseSlug = slugify.slugify(request.name());
        String uniqueSlug = baseSlug + "-" + UUID.randomUUID().toString().substring(0, 8);
        PostCategory postCategory = PostCategory.builder()
                .name(request.name().toUpperCase())
                .slug(uniqueSlug)
                .build();

        return postCategoryRepository.save(postCategory);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public PostCategorySummary updatePostCategory(UUID postCategoryId, UpdatePostCategoryRequest request) {
        PostCategory postCategory = getPostCategoryEntityById(postCategoryId);
        PostCategory oldValue = mapper.toClone(postCategory);

        boolean isExist = postCategoryRepository.existsByName(request.name().toUpperCase());
        if (isExist) {
            throw new PostCategoryExistsException(PostErrorEnums.POST_CATEGORY_EXISTS.getMessage());
        }

        PostCategory partialUpdate = mapper.partialUpdate(request, postCategory);
        Slugify slugify = Slugify.builder().build();
        String baseSlug = slugify.slugify(request.name());
        String uniqueSlug = baseSlug + "-" + UUID.randomUUID().toString().substring(0, 8);
        partialUpdate.setName(request.name().toUpperCase());
        partialUpdate.setSlug(uniqueSlug);

        PostCategory savedPostCategory = postCategoryRepository.save(partialUpdate);

        String userAgent = Objects.requireNonNull(HttpRequestUtils.getCurrentHttpRequest()).getHeader("User-Agent");
        String clientIp = HttpRequestUtils.getClientIpAddress(HttpRequestUtils.getCurrentHttpRequest());

        eventPublisher.publishEvent(new PostCategoryUpdate(
                this,
                savedPostCategory.getName(),
                ObjectHelper.extractChangesWithCommonsLang(oldValue, savedPostCategory),
                userAgent,
                clientIp,
                authService.getUserIdFromContext()
        ));

        return mapper.toDto(savedPostCategory);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void deletePostCategory(UUID postCategoryId) {
        PostCategory postCategory = getPostCategoryEntityById(postCategoryId);

        postCategoryRepository.delete(postCategory);
    }

    @Transactional
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void bulkDeletePostCategories(BulkPostCategoryDeleteRequest request) {
        postCategoryRepository.deleteAllById(request.postCategoryIds());
    }

    @Transactional
    public PostCategory getRefByName(String name) {
        return postCategoryRepository.getReferenceByName(name);
    }

    /**
     * Get all categories with their post counts
     *
     * @param pageable Pagination information
     * @return Page of categories with post counts
     */
    @Transactional(readOnly = true)
    public Page<PostCategoryWithCountDto> getCategoriesWithPostCount(Pageable pageable) {
        Page<PostCategory> categories = postCategoryRepository.findAll(pageable);

        List<PostCategoryWithCountDto> categoriesWithCounts = categories.getContent().stream()
                .map(category -> {
                    long postCount = postRepository.countByCategory(category);
                    return new PostCategoryWithCountDto(category, postCount);
                })
                .collect(Collectors.toList());

        return new PageImpl<>(categoriesWithCounts, pageable, categories.getTotalElements());
    }

    /**
     * Get a specific category with its post count by ID
     *
     * @param id Category ID
     * @return Category with post count
     */
    @Transactional(readOnly = true)
    public PostCategoryWithCountDto getCategoryWithCountById(UUID id) {
        PostCategory category = getPostCategoryEntityById(id);
        long postCount = postRepository.countByCategory(category);
        return new PostCategoryWithCountDto(category, postCount);
    }

    /**
     * Get a specific category with its post count by name
     *
     * @param name Category name
     * @return Category with post count
     */
    @Transactional(readOnly = true)
    public PostCategoryWithCountDto getCategoryWithCountByName(String name) {
        PostCategory category = getPostCategoryEntityByName(name);
        long postCount = postRepository.countByCategory(category);
        return new PostCategoryWithCountDto(category, postCount);
    }

    /**
     * Get all categories with their post counts (non-paginated)
     *
     * @return List of all categories with post counts
     */
    @Transactional(readOnly = true)
    public List<PostCategoryWithCountDto> getAllCategoriesWithPostCount() {
        List<PostCategory> categories = postCategoryRepository.findAll();

        return categories.stream()
                .map(category -> {
                    long postCount = postRepository.countByCategory(category);
                    return new PostCategoryWithCountDto(category, postCount);
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public PostCategory getRefById(UUID id) {
        return postCategoryRepository.getReferenceById(id);
    }
}
