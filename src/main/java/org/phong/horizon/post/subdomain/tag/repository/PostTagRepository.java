package org.phong.horizon.post.subdomain.tag.repository;

import org.phong.horizon.post.subdomain.tag.entity.PostTag;
import org.phong.horizon.post.subdomain.tag.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface PostTagRepository extends JpaRepository<PostTag, UUID> {

    List<PostTag> findByPostId(UUID postId);

    @Query("SELECT pt.tag FROM PostTag pt WHERE pt.post.id = :postId")
    List<Tag> findTagsByPostId(@Param("postId") UUID postId);

    @Modifying
    @Query("DELETE FROM PostTag pt WHERE pt.post.id = :postId")
    void deleteByPostId(@Param("postId") UUID postId);

    @Query("SELECT pt.tag.name FROM PostTag pt WHERE pt.post.id = :postId")
    List<String> findTagNamesByPostId(@Param("postId") UUID postId);

    boolean existsByPostIdAndTagId(UUID postId, UUID tagId);
}

