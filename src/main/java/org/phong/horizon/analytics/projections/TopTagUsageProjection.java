package org.phong.horizon.analytics.projections;

import java.time.LocalDate;
import java.util.UUID;

public interface TopTagUsageProjection {
    UUID getId();
    String getTagName();
    LocalDate getPostDate();
    Long getPostCount();
    Integer getRank();
}
