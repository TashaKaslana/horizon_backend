package org.phong.horizon.post.infrastructure.persistence.repositories;

import org.phong.horizon.core.enums.Visibility;
import org.phong.horizon.post.infrastructure.persistence.entities.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface PostRepository extends JpaRepository<Post, UUID> {
    List<Post> findAllByUser_Id(UUID userId);

    void deleteAllByUser_Id(UUID userId);

    List<Post> findAllByVisibility(Visibility visibility);

    Page<Post> findAllByUser_IdAndVisibility(Pageable page, UUID userId, Visibility visibility);

    @Modifying
    @Query("UPDATE Post p SET p.isAuthorDeleted = true WHERE p.user.id = :id")
    void softDeleteAllPostByUserId(UUID id);

    @Modifying
    @Query("UPDATE Post p SET p.isAuthorDeleted = false WHERE p.user.id = :id")
    void restoreAllPostByUserId(UUID id);

    Page<Post> findAllByVisibility(Visibility visibility, Pageable pageable);

    @Query("SELECT CASE WHEN EXISTS(SELECT 1 FROM Post p WHERE p.videoAsset.id = :assetId) THEN true ELSE false END")
    boolean isExistByAssetId(UUID assetId);

    Page<Post> findAllByVisibilityAndIdNot(Visibility visibility, UUID id, Pageable pageable);

    Page<Post> findAllByVisibilityAndCategoryName(Visibility visibility, String categoryName, Pageable pageable);

    Page<Post> findAllByVisibilityAndIdNotAndCategoryName(Visibility visibility, UUID excludePostId, String categoryName, Pageable pageable);

    Page<Post> findAllByUser_IdAndVisibilityAndIdNot(Pageable pageable, UUID userId, Visibility visibility, UUID excludePostId);

    long countAllByUserId(UUID userId);

    long countByCreatedAtAfter(Instant todayStart);

    long countByCreatedAtBetween(Instant yesterdayStart, Instant todayStart);

    @Query(value = """
                SELECT DATE(created_at) AS date, COUNT(*) AS count
                FROM posts
                WHERE created_at >= :from
                GROUP BY DATE(created_at)
                ORDER BY DATE(created_at)
            """, nativeQuery = true)
    List<Object[]> countPostsPerDay(Instant from);
}