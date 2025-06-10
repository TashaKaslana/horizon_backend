package org.phong.horizon.post.subdomain.tag.service;

import com.github.slugify.Slugify;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.phong.horizon.core.services.AuthService;
import org.phong.horizon.post.subdomain.tag.dto.BulkTagDeleteRequest;
import org.phong.horizon.post.subdomain.tag.dto.CreateTagRequest;
import org.phong.horizon.post.subdomain.tag.dto.TagResponse;
import org.phong.horizon.post.subdomain.tag.dto.UpdateTagRequest;
import org.phong.horizon.post.subdomain.tag.dto.TagWithCountDto;
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

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

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

        // Check for existing name
        tagRepository.findByName(createTagRequest.getName()).ifPresent(_ -> {
            throw new TagPropertyConflictException("Name already exists: " + createTagRequest.getName());
        });

        Slugify slugify = Slugify.builder().build();
        String baseSlug = slugify.slugify(createTagRequest.getName());
        String uniqueSlug = baseSlug + "-" + UUID.randomUUID().toString().substring(0, 8);

        Tag tag = tagMapper.toTag(createTagRequest);
        tag.setSlug(uniqueSlug);

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

        // Check for name conflict if name is being updated and is different from the current one
        if (StringUtils.hasText(updateTagRequest.getName()) && !Objects.equals(tag.getName(), updateTagRequest.getName())) {
            tagRepository.findByName(updateTagRequest.getName()).ifPresent(existingTag -> {
                if (!existingTag.getId().equals(tagId)) {
                    throw new TagPropertyConflictException("Name already exists: " + updateTagRequest.getName());
                }
            });
        }

        //TODO: ensure slug is unique and update is not same
        Slugify slugify = Slugify.builder().build();
        String baseSlug = slugify.slugify(updateTagRequest.getName());
        String uniqueSlug = baseSlug + "-" + UUID.randomUUID().toString().substring(0, 8);

        Tag preUpdate = tagMapper.updateTagFromRequest(updateTagRequest, tag);
        preUpdate.setSlug(uniqueSlug);

        Tag updatedTag = tagRepository.save(preUpdate);
        log.info("Tag with id {} updated by user {}", updatedTag.getId(), currentUserId);
        return tagMapper.toTagResponse(updatedTag);
    }

    @Transactional
    public void deleteTag(UUID tagId) {
        UUID currentUserId = authService.getUserIdFromContext();
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new TagNotFoundException("Tag not found with id: " + tagId));

        tagRepository.delete(tag);
        log.debug("Tag with id {} deleted by user {}", tag.getId(), currentUserId);
    }

    /**
     * Get a tag by name or create a new one if it doesn't exist
     *
     * @param tagName The tag name
     * @return The existing or newly created tag entity
     */
    @Transactional
    public Tag getOrCreateTag(String tagName) {
        if (tagName == null || tagName.isBlank()) {
            throw new IllegalArgumentException("Tag name cannot be null or blank");
        }

        // Normalize tag name (trim and convert to lowercase)
        String normalizedTagName = tagName.trim().toLowerCase();

        return tagRepository.findByName(normalizedTagName)
                .orElseGet(() -> {
                    // Create new tag if it doesn't exist
                    log.debug("Creating new tag with name: {}", normalizedTagName);

                    Slugify slugify = Slugify.builder().build();
                    String baseSlug = slugify.slugify(normalizedTagName);
                    String uniqueSlug = baseSlug + "-" + UUID.randomUUID().toString().substring(0, 8);

                    Tag newTag = new Tag();
                    newTag.setName(normalizedTagName);
                    newTag.setSlug(uniqueSlug);
                    return tagRepository.save(newTag);
                });
    }

    /**
     * Get a tag entity reference by ID without fully loading the entity
     *
     * @param tagId The tag ID
     * @return The tag entity reference
     */
    @Transactional(readOnly = true)
    public Tag getRefById(UUID tagId) {
        return tagRepository.getReferenceById(tagId);
    }

    /**
     * Find a tag by name
     *
     * @param tagName The tag name
     * @return The tag entity if found, or null if not found
     */
    @Transactional(readOnly = true)
    public Tag findByName(String tagName) {
        return tagRepository.findByName(tagName).orElse(null);
    }

    /**
     * Convert Tag entity to TagResponse
     *
     * @param tag The Tag entity to convert
     * @return The TagResponse DTO
     */
    public TagResponse convertToResponse(Tag tag) {
        return tagMapper.toTagResponse(tag);
    }

    /**
     * Get all tags with their post counts
     *
     * @param pageable Pagination information
     * @return Page of TagWithCountDto
     */
    @Transactional(readOnly = true)
    public Page<TagWithCountDto> getTagsWithPostCount(Pageable pageable) {
        return tagRepository.findAll(pageable)
                .map(tag -> {
                    long count = tagRepository.countPostsByTagId(tag.getId());
                    return new TagWithCountDto(tag, count);
                });
    }

    /**
     * Get all tags with their post counts (no pagination)
     *
     * @return List of TagWithCountDto
     */
    @Transactional(readOnly = true)
    public List<TagWithCountDto> getAllTagsWithPostCount() {
        return tagRepository.findAll().stream()
                .map(tag -> {
                    long count = tagRepository.countPostsByTagId(tag.getId());
                    return new TagWithCountDto(tag, count);
                })
                .collect(Collectors.toList());
    }

    /**
     * Get a tag with post count by ID
     *
     * @param tagId The tag ID
     * @return TagWithCountDto
     */
    @Transactional(readOnly = true)
    public TagWithCountDto getTagWithCountById(UUID tagId) {
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new TagNotFoundException("Tag not found with id: " + tagId));
        long count = tagRepository.countPostsByTagId(tag.getId());
        return new TagWithCountDto(tag, count);
    }

    /**
     * Get a tag with post count by slug
     *
     * @param slug The tag slug
     * @return TagWithCountDto
     */
    @Transactional(readOnly = true)
    public TagWithCountDto getTagWithCountBySlug(String slug) {
        Tag tag = tagRepository.findBySlug(slug)
                .orElseThrow(() -> new TagNotFoundException("Tag not found with slug: " + slug));
        long count = tagRepository.countPostsByTagId(tag.getId());
        return new TagWithCountDto(tag, count);
    }

    /**
     * Get a tag with post count by name
     *
     * @param name The tag name
     * @return TagWithCountDto
     */
    @Transactional(readOnly = true)
    public TagWithCountDto getTagWithCountByName(String name) {
        Tag tag = tagRepository.findByName(name)
                .orElseThrow(() -> new TagNotFoundException("Tag not found with name: " + name));
        long count = tagRepository.countPostsByTagId(tag.getId());
        return new TagWithCountDto(tag, count);
    }

    public void bulkDeleteTags(@Valid BulkTagDeleteRequest request) {
        tagRepository.deleteAllById(request.tagIds());
    }
}
