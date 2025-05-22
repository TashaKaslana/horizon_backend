package org.phong.horizon.post.subdomain.tag.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.phong.horizon.core.services.AuthService;
import org.phong.horizon.post.subdomain.tag.dto.CreateTagRequest;
import org.phong.horizon.post.subdomain.tag.dto.TagResponse;
import org.phong.horizon.post.subdomain.tag.dto.UpdateTagRequest;
import org.phong.horizon.post.subdomain.tag.entity.Tag;
import org.phong.horizon.post.subdomain.tag.exception.TagNotFoundException;
import org.phong.horizon.post.subdomain.tag.exception.TagPropertyConflictException;
import org.phong.horizon.post.subdomain.tag.mapper.TagMapper;
import org.phong.horizon.post.subdomain.tag.repository.TagRepository;
import org.phong.horizon.post.subdomain.tag.specification.TagSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;
    private final TagMapper tagMapper;
    private final AuthService authService;

    @Transactional
    public TagResponse createTag(CreateTagRequest createTagRequest) {
        UUID currentUserId = authService.getUserIdFromContext();

        // Check for existing slug
        tagRepository.findBySlug(createTagRequest.getSlug()).ifPresent(_ -> {
            throw new TagPropertyConflictException("Slug already exists: " + createTagRequest.getSlug());
        });

        // Check for existing name
        tagRepository.findByName(createTagRequest.getName()).ifPresent(_ -> {
            throw new TagPropertyConflictException("Name already exists: " + createTagRequest.getName());
        });

        Tag tag = tagMapper.toTag(createTagRequest);
        tag.setCreatedBy(currentUserId);
        tag.setUpdatedBy(currentUserId); // Typically, on creation, createdBy and updatedBy are the same

        Tag savedTag = tagRepository.save(tag);
        log.info("Tag with id {} created by user {}", savedTag.getId(), currentUserId);
        return tagMapper.toTagResponse(savedTag);
    }

    @Transactional(readOnly = true)
    public TagResponse getTagById(UUID tagId) {
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new TagNotFoundException("Tag not found with id: " + tagId));
        return tagMapper.toTagResponse(tag);
    }

    @Transactional(readOnly = true)
    public Page<TagResponse> getAllTags(String name, String slug, Pageable pageable) {
        Specification<Tag> spec = Specification.where(TagSpecification.nameContains(name))
                                             .and(TagSpecification.slugEquals(slug));
        return tagRepository.findAll(spec, pageable)
                .map(tagMapper::toTagResponse);
    }

    @Transactional
    public TagResponse updateTag(UUID tagId, UpdateTagRequest updateTagRequest) {
        UUID currentUserId = authService.getUserIdFromContext();

        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new TagNotFoundException("Tag not found with id: " + tagId));

        // Check for slug conflict if slug is being updated and is different from the current one
        if (StringUtils.hasText(updateTagRequest.getSlug()) && !Objects.equals(tag.getSlug(), updateTagRequest.getSlug())) {
            tagRepository.findBySlug(updateTagRequest.getSlug()).ifPresent(existingTag -> {
                if (!existingTag.getId().equals(tagId)) {
                    throw new TagPropertyConflictException("Slug already exists: " + updateTagRequest.getSlug());
                }
            });
        }

        // Check for name conflict if name is being updated and is different from the current one
        if (StringUtils.hasText(updateTagRequest.getName()) && !Objects.equals(tag.getName(), updateTagRequest.getName())) {
            tagRepository.findByName(updateTagRequest.getName()).ifPresent(existingTag -> {
                if (!existingTag.getId().equals(tagId)) {
                    throw new TagPropertyConflictException("Name already exists: " + updateTagRequest.getName());
                }
            });
        }

        tagMapper.updateTagFromRequest(updateTagRequest, tag);
        tag.setUpdatedBy(currentUserId);

        Tag updatedTag = tagRepository.save(tag);
        log.info("Tag with id {} updated by user {}", updatedTag.getId(), currentUserId);
        return tagMapper.toTagResponse(updatedTag);
    }
}

