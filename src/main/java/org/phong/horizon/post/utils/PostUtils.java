package org.phong.horizon.post.utils;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.mapstruct.Named;
import org.phong.horizon.post.infrastructure.persistence.repositories.PostInteractionRepository;
import org.phong.horizon.post.infrastructure.persistence.repositories.ViewRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PostUtils {
    ViewRepository viewRepository;
    PostInteractionRepository postInteractionRepository;

    @Named("postViewCount")
    public long postViewCount(UUID id) {
        return viewRepository.countAllByPostId(id);
    }

    @Named("postInteractionCount")
    public long postInteractionCount(UUID id) {
        return postInteractionRepository.countAllByPost_Id(id);
    }
}
