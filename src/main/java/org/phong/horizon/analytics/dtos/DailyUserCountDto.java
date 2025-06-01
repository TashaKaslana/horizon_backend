package org.phong.horizon.analytics.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DailyUserCountDto {
    private LocalDate date;
    private long count;
}
