package org.phong.horizon.post.dtos;

import java.io.Serializable;
import java.util.List;

/**
 * DTO for {@link org.phong.horizon.post.infraustructure.persistence.entities.Post}
 */
public record UpdatePostRequest(String caption, String description,
                                Double duration,
                                String visibility, List<String> tags) implements Serializable {
}