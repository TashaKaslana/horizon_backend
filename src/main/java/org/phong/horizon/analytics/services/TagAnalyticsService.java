package org.phong.horizon.analytics.services;

import lombok.AllArgsConstructor;
import org.phong.horizon.analytics.dtos.DailyCountDto;
import org.phong.horizon.analytics.dtos.OverviewStatistic;
import org.phong.horizon.core.services.LocalizationProvider;
import org.phong.horizon.analytics.dtos.TopTagUsageDTO;
import org.phong.horizon.analytics.projections.TopTagUsageProjection;
import org.phong.horizon.post.subdomain.tag.entity.Tag;
import org.phong.horizon.post.subdomain.tag.repository.TagRepository;
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
public class TagAnalyticsService {
    private final TagRepository tagRepository;

    /**
     * Get overview statistics for tags
     */
    @Transactional(readOnly = true)
    @Cacheable("tag-overview")
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
                LocalizationProvider.getMessage("analytics.tag.total.title"),
                String.valueOf(totalTags),
                tagTrend,
                tagTrend > 0
                        ? LocalizationProvider.getMessage("analytics.tag.total.message.growing")
                        : LocalizationProvider.getMessage("analytics.tag.total.message.stable"),
                LocalizationProvider.getMessage("analytics.tag.total.description")
            ),
            new OverviewStatistic(
                LocalizationProvider.getMessage("analytics.tag.most_popular.title"),
                mostUsedTagName,
                0.0,
                LocalizationProvider.getMessage("analytics.tag.most_popular.message", mostUsedTagCount),
                LocalizationProvider.getMessage("analytics.tag.most_popular.description")
            ),
            new OverviewStatistic(
                LocalizationProvider.getMessage("analytics.tag.unused.title"),
                String.valueOf(unusedTags),
                0.0,
                unusedTags > 0
                        ? LocalizationProvider.getMessage("analytics.tag.unused.message.cleanup")
                        : LocalizationProvider.getMessage("analytics.tag.unused.message.in_use"),
                LocalizationProvider.getMessage("analytics.tag.unused.description")
            ),
            new OverviewStatistic(
                LocalizationProvider.getMessage("analytics.tag.new_today.title"),
                String.valueOf(tagsToday),
                tagTrend,
                tagsToday > 0
                        ? LocalizationProvider.getMessage("analytics.tag.new_today.message.active")
                        : LocalizationProvider.getMessage("analytics.tag.new_today.message.none"),
                LocalizationProvider.getMessage("analytics.tag.new_today.description")
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
    @Cacheable(value = "tag-daily-counts", key = "#days")
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
