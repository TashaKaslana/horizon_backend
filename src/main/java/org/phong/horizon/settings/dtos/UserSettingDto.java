package org.phong.horizon.settings.dtos;

import java.util.Map;

/**
 * A flexible representation of user preferences. New settings can be added as
 * key/value pairs in the {@code preferences} map.
 */
public record UserSettingDto(Map<String, Object> preferences) {
}
