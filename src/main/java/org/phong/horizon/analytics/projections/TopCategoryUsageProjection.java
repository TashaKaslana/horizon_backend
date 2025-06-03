package org.phong.horizon.analytics.projections;

import java.time.LocalDate;
import java.util.UUID;

public interface TopCategoryUsageProjection {
    UUID getId();
    String getCategoryName();
    LocalDate getPostDate();
    Long getPostCount();
    Integer getRank();
}