package org.phong.horizon.analytics.services;

import lombok.AllArgsConstructor;
import org.phong.horizon.analytics.dtos.DailyCountDto;
import org.phong.horizon.analytics.dtos.OverviewStatistic;
import org.phong.horizon.analytics.dtos.TopCategoryUsageDTO;
import org.phong.horizon.post.subdomain.category.entities.PostCategory;
import org.phong.horizon.post.subdomain.category.repositories.PostCategoryRepository;
import org.phong.horizon.post.infrastructure.persistence.repositories.PostRepository;
import org.phong.horizon.analytics.projections.TopCategoryUsageProjection;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

import static org.phong.horizon.analytics.utils.AnalyticsHelper.calculateTrend;
import static org.phong.horizon.analytics.utils.AnalyticsHelper.getDailyCountDtos;

@Service
@AllArgsConstructor
public class CategoryAnalyticsService {
    private final PostCategoryRepository categoryRepository;
    private final PostRepository postRepository;

    /**
     * Get overview statistics for categories
     */
    @Transactional(readOnly = true)
    @Cacheable("category-overview")
    public List<OverviewStatistic> getCategoryAnalytics() {
        ZoneId zone = ZoneOffset.UTC;

        // Total categories
        long totalCategories = categoryRepository.count();

        // Today's new categories
        Instant todayStart = LocalDate.now(zone).atStartOfDay(zone).toInstant();
        long categoriesToday = categoryRepository.countByCreatedAtAfter(todayStart);

        // Yesterday's new categories
        Instant yesterdayStart = LocalDate.now(zone).minusDays(1).atStartOfDay(zone).toInstant();
        long categoriesYesterday = categoryRepository.countByCreatedAtBetween(yesterdayStart, todayStart);

        double categoryTrend = calculateTrend(categoriesToday, categoriesYesterday);

        // Most used category
        PostCategory mostUsedCategory = categoryRepository.findMostUsedCategory();
        String mostUsedCategoryName = mostUsedCategory != null ? mostUsedCategory.getName() : "None";
        long mostUsedCategoryCount = mostUsedCategory != null ? postRepository.countByCategory(mostUsedCategory) : 0;

        // Unused categories count
        long unusedCategories = categoryRepository.countUnusedCategories();

        return List.of(
            new OverviewStatistic(
                "Total Categories",
                String.valueOf(totalCategories),
                categoryTrend,
                categoryTrend > 0 ? "Growing" : "Stable",
                "Total number of categories in the system"
            ),
            new OverviewStatistic(
                "Most Used Category",
                mostUsedCategoryName,
                0.0,
                String.format("Used in %d posts", mostUsedCategoryCount),
                "Category with the most posts assigned"
            ),
            new OverviewStatistic(
                "Unused Categories",
                String.valueOf(unusedCategories),
                0.0,
                unusedCategories > 0 ? "Candidates for cleanup" : "All categories in use",
                "Categories without any posts"
            ),
            new OverviewStatistic(
                "New Categories Today",
                String.valueOf(categoriesToday),
                categoryTrend,
                categoriesToday > 0 ? "Active category creation" : "No new categories today",
                "Categories created in the last 24 hours"
            )
        );
    }

    /**
     * Get daily category creation counts for the specified number of days
     *
     * @param days Number of days to include in the result
     * @return List of daily category counts
     */
    @Transactional(readOnly = true)
    public List<DailyCountDto> getDailyCategoryCounts(int days) {
        Instant startDate = LocalDate.now().minusDays(days - 1).atStartOfDay(ZoneOffset.UTC).toInstant();
        List<Object[]> results = categoryRepository.countCategoriesPerDay(startDate);
        return getDailyCountDtos(days, results);
    }

    /**
     * Get daily category counts with zeros filled in for days with no data
     *
     * @param days Number of days to include
     * @return List of daily count DTOs with zeros for missing days
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "category-daily-counts", key = "#days")
    public List<DailyCountDto> getFilledDailyCategoryCounts(int days) {
        return getDailyCategoryCounts(days);
    }

    /**
     * Get category post distribution - categories and their post counts
     *
     * @param days Number of days to include in the calculation (default: 30)
     * @return List of category names to post counts
     */
    @Transactional(readOnly = true)
    public List<TopCategoryUsageDTO> getCategoryDistribution(int days) {
        Instant endDate = Instant.now();
        Instant startDate = endDate.minus(Duration.ofDays(days));
        return getTopCategoryUsage(startDate, endDate);
    }

    @Transactional(readOnly = true)
    public List<TopCategoryUsageDTO> getTopCategoryUsage(Instant startDate, Instant endDate) {
        List<TopCategoryUsageProjection> projections =
                categoryRepository.getTop10CategoryUsagePerDay(startDate, endDate);

        // Step 1: Collect all distinct dates and categories
        Set<String> allCategories = projections.stream()
                .map(TopCategoryUsageProjection::getCategoryName)
                .collect(Collectors.toSet());

        Set<LocalDate> allDates = getAllDatesBetween(startDate, endDate);

        // Step 2: Group projections by (date -> category -> projection)
        Map<LocalDate, Map<String, TopCategoryUsageProjection>> grouped = new HashMap<>();

        for (TopCategoryUsageProjection p : projections) {
            LocalDate date = p.getPostDate();
            grouped.computeIfAbsent(date, d -> new HashMap<>()).put(p.getCategoryName(), p);
        }

        // Step 3: Fill missing dates/categories
        List<TopCategoryUsageDTO> result = new ArrayList<>();

        for (LocalDate date : allDates) {
            Map<String, TopCategoryUsageProjection> categoryMap = grouped.getOrDefault(date, Map.of());

            for (String category : allCategories) {
                TopCategoryUsageProjection p = categoryMap.get(category);
                if (p != null) {
                    result.add(new TopCategoryUsageDTO(
                            p.getId(),
                            p.getCategoryName(),
                            p.getPostDate(),
                            p.getPostCount(),
                            p.getRank()
                    ));
                } else {
                    result.add(new TopCategoryUsageDTO(
                            null,
                            category,
                            date.atStartOfDay().toLocalDate(),
                            0L,
                            0
                    ));
                }
            }
        }

        return result;
    }

    private Set<LocalDate> getAllDatesBetween(Instant start, Instant end) {
        LocalDate startDate = start.atZone(ZoneOffset.UTC).toLocalDate();
        LocalDate endDate = end.atZone(ZoneOffset.UTC).toLocalDate();

        Set<LocalDate> dates = new LinkedHashSet<>();
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            dates.add(date);
        }
        return dates;
    }
}
