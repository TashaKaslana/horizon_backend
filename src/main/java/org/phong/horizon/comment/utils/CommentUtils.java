package org.phong.horizon.comment.utils;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.mapstruct.Named;
import org.phong.horizon.comment.infrastructure.persistence.repositories.CommentInteractionRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class CommentUtils {
    CommentInteractionRepository commentInteractionRepository;

    @Named("commentInteractionCount")
    public long commentInteractionCount(UUID commentId) {
        return commentInteractionRepository.countAllByComment_Id(commentId);
    }
}
