package org.phong.horizon.historyactivity.dtos;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ActivityDisplayDto(
    UUID id,
    List<ActivityPart> parts,
    Instant timestamp
) {}