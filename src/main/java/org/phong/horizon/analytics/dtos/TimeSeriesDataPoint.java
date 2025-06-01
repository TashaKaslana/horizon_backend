package org.phong.horizon.analytics.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a time series data point with a label and value.
 * Used for analytics charts that show metrics over time.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TimeSeriesDataPoint {
    private String label;
    private long value;
}
