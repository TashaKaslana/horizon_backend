package org.phong.horizon.analytics.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
public class TopTagUsageDTO {
    private UUID id;
    private String tagName;
    private LocalDate postDate;
    private Long postCount;
    private Integer rank;
}
