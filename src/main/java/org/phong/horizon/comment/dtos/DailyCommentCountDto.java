package org.phong.horizon.comment.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DailyCommentCountDto {
    private LocalDate date;
    private long count;
}
