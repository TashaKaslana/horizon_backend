package org.phong.horizon.post.subdomain.category.repositories;

import org.phong.horizon.analytics.projections.TopCategoryUsageProjection;
import org.phong.horizon.post.subdomain.category.entities.PostCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PostCategoryRepository extends JpaRepository<PostCategory, UUID> {
    PostCategory getReferenceByName(String name);

    boolean existsByName(String name);

    Optional<PostCategory> findByName(String name);

    long countByCreatedAtAfter(Instant createdAt);

    long countByCreatedAtBetween(Instant createdAtStart, Instant createdAtEnd);

    /**
     * Find the category with the most posts assigned to it
     */
    @Query(value = """
    SELECT pc.* FROM post_categories pc
    WHERE pc.id = (
        SELECT p.id FROM posts p
        GROUP BY p.id
        ORDER BY COUNT(*) DESC
        LIMIT 1
    )
    """, nativeQuery = true)
    PostCategory findMostUsedCategory();


    /**
     * Count posts in a specific category
     */
    @Query("SELECT COUNT(p) FROM Post p WHERE p.category.id = :categoryId")
    long countByCategory(@Param("categoryId") PostCategory category);

    /**
     * Count categories that don't have any posts
     */
    @Query("SELECT COUNT(pc) FROM PostCategory pc WHERE pc.id NOT IN " +
           "(SELECT DISTINCT p.category.id FROM Post p WHERE p.category IS NOT NULL)")
    long countUnusedCategories();

    /**
     * Count categories created per day
     */
    @Query("SELECT FUNCTION('DATE', pc.createdAt), COUNT(pc.id) " +
           "FROM PostCategory pc " +
           "WHERE pc.createdAt >= :startDate " +
           "GROUP BY FUNCTION('DATE', pc.createdAt) " +
           "ORDER BY FUNCTION('DATE', pc.createdAt)")
    List<Object[]> countCategoriesPerDay(@Param("startDate") Instant startDate);

    @Query(value = """
             SELECT *
             FROM (
                 SELECT\s
                     pc.id,
                     pc.name AS category_name,
                     DATE(p.created_at) AS post_date,
                     COUNT(p.id) AS post_count,
                     RANK() OVER (PARTITION BY DATE(p.created_at) ORDER BY COUNT(p.id) DESC) AS rank
                 FROM posts p
                 JOIN post_categories pc ON p.category_id = pc.id
                 WHERE p.created_at BETWEEN :startDate AND :endDate
                 GROUP BY pc.id, pc.name, DATE(p.created_at)
             ) ranked
             WHERE ranked.rank <= 10
             ORDER BY ranked.post_date DESC, ranked.rank
            \s""", nativeQuery = true)
    List<TopCategoryUsageProjection> getTop10CategoryUsagePerDay(
            @Param("startDate") Instant startDate,
            @Param("endDate") Instant endDate
    );
}

