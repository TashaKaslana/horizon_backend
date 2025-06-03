package org.phong.horizon.post.subdomain.tag.repository;

import org.phong.horizon.analytics.projections.TopTagUsageProjection;
import org.phong.horizon.post.subdomain.tag.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TagRepository extends JpaRepository<Tag, UUID>, JpaSpecificationExecutor<Tag> {
    Optional<Tag> findBySlug(String slug);
    Optional<Tag> findByName(String name);

    long countByCreatedAtAfter(Instant createdAt);

    long countByCreatedAtBetween(Instant createdAtStart, Instant createdAtEnd);

    /**
     * Find the tag with the most posts assigned to it
     */
    @Query(value = "SELECT t.* FROM tags t " +
           "LEFT JOIN (SELECT jsonb_array_elements_text(tags) AS tag_name, COUNT(*) AS post_count " +
           "FROM posts GROUP BY tag_name) AS tag_counts " +
           "ON t.name = tag_counts.tag_name " +
           "ORDER BY COALESCE(tag_counts.post_count, 0) DESC LIMIT 1", nativeQuery = true)
    Tag findMostUsedTag();

    /**
     * Count posts associated with a specific tag name
     */
    @Query(value = "SELECT COUNT(*) FROM posts " +
           "WHERE tags @> CAST(:tagName AS jsonb)", nativeQuery = true)
    long countPostsByTagName(@Param("tagName") String tagName);

    /**
     * Count posts associated with a specific tag by ID
     */
    @Query(value = "SELECT COUNT(*) FROM posts p, tags t " +
           "WHERE t.id = :tagId AND p.tags @> to_jsonb(t.name::text)::jsonb", nativeQuery = true)
    long countPostsByTag(@Param("tagId") UUID tagId);

    /**
     * Count the number of posts associated with a specific tag
     *
     * @param tagId The tag ID
     * @return The count of posts
     */
    @Query("SELECT COUNT(pt.post) FROM PostTag pt WHERE pt.tag.id = :tagId")
    long countPostsByTagId(@Param("tagId") UUID tagId);

    /**
     * Count tags that don't have any posts
     */
    @Query(value = "SELECT COUNT(*) FROM tags t " +
           "WHERE NOT EXISTS (SELECT 1 FROM posts p " +
           "WHERE p.tags @> to_jsonb(t.name::text)::jsonb)", nativeQuery = true)
    long countUnusedTags();

    /**
     * Count tags created per day
     */
    @Query("SELECT FUNCTION('DATE', t.createdAt), COUNT(t.id) " +
           "FROM Tag t " +
           "WHERE t.createdAt >= :startDate " +
           "GROUP BY FUNCTION('DATE', t.createdAt) " +
           "ORDER BY FUNCTION('DATE', t.createdAt)")
    List<Object[]> countTagsPerDay(@Param("startDate") Instant startDate);

    /**
     * Get top 10 tag usage per day within date range
     *
     * @param startDate Start date for filtering posts
     * @param endDate End date for filtering posts
     * @return List of top tag usage projections
     */
    @Query(value = "WITH tag_usage AS ( " +
           "    SELECT " +
           "        t.id, " +
           "        t.name AS tag_name, " +
           "        DATE(p.created_at) AS post_date, " +
           "        COUNT(*) AS post_count, " +
           "        RANK() OVER (PARTITION BY DATE(p.created_at) ORDER BY COUNT(*) DESC) AS rank " +
           "    FROM " +
           "        tags t, " +
           "        posts p " +
           "    WHERE " +
           "        p.tags @> to_jsonb(t.name::text)::jsonb " +
           "        AND p.created_at >= :startDate " +
           "        AND p.created_at <= :endDate " +
           "    GROUP BY " +
           "        t.id, t.name, DATE(p.created_at) " +
           ") " +
           "SELECT " +
           "    id, " +
           "    tag_name, " +
           "    post_date, " +
           "    post_count, " +
           "    rank " +
           "FROM " +
           "    tag_usage " +
           "WHERE " +
           "    rank <= 10 " +
           "ORDER BY " +
           "    post_date, rank",
           nativeQuery = true)
    List<TopTagUsageProjection> getTop10TagUsagePerDay(
            @Param("startDate") Instant startDate,
            @Param("endDate") Instant endDate);
}
