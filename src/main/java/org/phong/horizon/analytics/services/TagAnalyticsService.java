package org.phong.horizon.analytics.services;

import lombok.AllArgsConstructor;
import org.phong.horizon.analytics.dtos.DailyCountDto;
import org.phong.horizon.analytics.dtos.OverviewStatistic;
import org.phong.horizon.analytics.dtos.TopTagUsageDTO;
import org.phong.horizon.analytics.projections.TopTagUsageProjection;
import org.phong.horizon.post.subdomain.tag.entity.Tag;
import org.phong.horizon.post.subdomain.tag.repository.TagRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

import static org.phong.horizon.analytics.utils.AnalyticsHelper.calculateTrend;
import static org.phong.horizon.analytics.utils.AnalyticsHelper.getDailyCountDtos;

@Service
@AllArgsConstructor
public class TagAnalyticsService {
    private final TagRepository tagRepository;

    /**
     * Get overview statistics for tags
     */
    @Transactional(readOnly = true)
    public List<OverviewStatistic> getTagAnalytics() {
        ZoneId zone = ZoneOffset.UTC;

        // Total tags
        long totalTags = tagRepository.count();

        // Today's new tags
        Instant todayStart = LocalDate.now(zone).atStartOfDay(zone).toInstant();
        long tagsToday = tagRepository.countByCreatedAtAfter(todayStart);

        // Yesterday's new tags
        Instant yesterdayStart = LocalDate.now(zone).minusDays(1).atStartOfDay(zone).toInstant();
        long tagsYesterday = tagRepository.countByCreatedAtBetween(yesterdayStart, todayStart);

        double tagTrend = calculateTrend(tagsToday, tagsYesterday);

        // Most popular tags
        Tag mostUsedTag = tagRepository.findMostUsedTag();
        String mostUsedTagName = mostUsedTag != null ? mostUsedTag.getName() : "None";
        long mostUsedTagCount = mostUsedTag != null ? tagRepository.countPostsByTagId(mostUsedTag.getId()) : 0;

        // Unused tags count
        long unusedTags = tagRepository.countUnusedTags();

        return List.of(
            new OverviewStatistic(
                "Total Tags",
                String.valueOf(totalTags),
                tagTrend,
                tagTrend > 0 ? "Growing" : "Stable",
                "Total number of tags in the system"
            ),
            new OverviewStatistic(
                "Most Popular Tag",
                mostUsedTagName,
                0.0,
                String.format("Used in %d posts", mostUsedTagCount),
                "Tag with the most posts assigned"
            ),
            new OverviewStatistic(
                "Unused Tags",
                String.valueOf(unusedTags),
                0.0,
                unusedTags > 0 ? "Candidates for cleanup" : "All tags in use",
                "Tags not used in any posts"
            ),
            new OverviewStatistic(
                "New Tags Today",
                String.valueOf(tagsToday),
                tagTrend,
                tagsToday > 0 ? "Active tag creation" : "No new tags today",
                "Tags created in the last 24 hours"
            )
        );
    }

    /**
     * Get daily tag creation counts for the specified number of days
     *
     * @param days Number of days to include in the result
     * @return List of daily tag counts
     */
    @Transactional(readOnly = true)
    public List<DailyCountDto> getDailyTagCounts(int days) {
        Instant startDate = LocalDate.now().minusDays(days - 1).atStartOfDay(ZoneOffset.UTC).toInstant();
        List<Object[]> results = tagRepository.countTagsPerDay(startDate);
        return getDailyCountDtos(days, results);
    }

    /**
     * Get tag distribution - tags and their post counts for a specified number of days
     *
     * @param days Number of days to include in the calculation (default: 30)
     * @return List of top tag usage DTOs
     */
    @Transactional(readOnly = true)
    public List<TopTagUsageDTO> getTagDistribution(int days) {
        Instant endDate = Instant.now();
        Instant startDate = endDate.minus(Duration.ofDays(days));
        return getTopTagUsage(startDate, endDate);
    }

    /**
     * Get top tag usage data between two dates
     *
     * @param startDate Start date for the analysis
     * @param endDate End date for the analysis
     * @return List of top tag usage DTOs with filled in dates
     */
    @Transactional(readOnly = true)
    public List<TopTagUsageDTO> getTopTagUsage(Instant startDate, Instant endDate) {
        List<TopTagUsageProjection> projections =
                tagRepository.getTop10TagUsagePerDay(startDate, endDate);

        // Step 1: Collect all distinct dates and tags
        Set<String> allTags = projections.stream()
                .map(TopTagUsageProjection::getTagName)
                .collect(Collectors.toSet());

        Set<LocalDate> allDates = getAllDatesBetween(startDate, endDate);

        // Step 2: Group projections by (date -> tag -> projection)
        Map<LocalDate, Map<String, TopTagUsageProjection>> grouped = new HashMap<>();

        for (TopTagUsageProjection p : projections) {
            LocalDate date = p.getPostDate();
            grouped.computeIfAbsent(date, _ -> new HashMap<>()).put(p.getTagName(), p);
        }

        // Step 3: Fill missing dates/tags
        List<TopTagUsageDTO> result = new ArrayList<>();

        for (LocalDate date : allDates) {
            Map<String, TopTagUsageProjection> tagMap = grouped.getOrDefault(date, Map.of());

            for (String tag : allTags) {
                TopTagUsageProjection p = tagMap.get(tag);
                if (p != null) {
                    result.add(new TopTagUsageDTO(
                            p.getId(),
                            p.getTagName(),
                            p.getPostDate(),
                            p.getPostCount(),
                            p.getRank()
                    ));
                } else {
                    result.add(new TopTagUsageDTO(
                            null,
                            tag,
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
