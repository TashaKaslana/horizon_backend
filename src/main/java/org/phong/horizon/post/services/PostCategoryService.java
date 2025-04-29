package org.phong.horizon.post.services;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.phong.horizon.core.services.AuthService;
import org.phong.horizon.core.utils.HttpRequestUtils;
import org.phong.horizon.core.utils.ObjectHelper;
import org.phong.horizon.post.dtos.CreatePostCategoryRequest;
import org.phong.horizon.post.dtos.PostCategorySummary;
import org.phong.horizon.post.dtos.UpdatePostCategoryRequest;
import org.phong.horizon.post.enums.PostErrorEnums;
import org.phong.horizon.post.events.PostCategoryUpdate;
import org.phong.horizon.post.exceptions.PostCategoryExistsException;
import org.phong.horizon.post.exceptions.PostCategoryNotFoundException;
import org.phong.horizon.post.infrastructure.mapstruct.PostCategoryMapper;
import org.phong.horizon.post.infrastructure.persistence.entities.PostCategory;
import org.phong.horizon.post.infrastructure.persistence.repositories.PostCategoryRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PostCategoryService {
    PostCategoryRepository postCategoryRepository;
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

        return postCategoryRepository.save(PostCategory.builder().name(request.name().toUpperCase()).build());
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
        partialUpdate.setName(request.name().toUpperCase());

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
}
