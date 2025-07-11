package org.phong.horizon.analytics.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OverviewStatistic {
    private String title;
    private String value;
    private double trend;
    private String message;
    private String description;
}
