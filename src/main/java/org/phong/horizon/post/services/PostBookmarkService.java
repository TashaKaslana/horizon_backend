package org.phong.horizon.post.services;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.phong.horizon.core.services.AuthService;
import org.phong.horizon.post.enums.BookmarkErrorEnums;
import org.phong.horizon.post.exceptions.BookmarkExistsException;
import org.phong.horizon.post.exceptions.BookmarkNotFoundException;
import org.phong.horizon.post.infrastructure.persistence.entities.Bookmark;
import org.phong.horizon.post.infrastructure.persistence.entities.Post;
import org.phong.horizon.post.infrastructure.persistence.repositories.BookmarkRepository;
import org.phong.horizon.user.infrastructure.persistence.entities.User;
import org.phong.horizon.user.services.UserService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class PostBookmarkService {
    BookmarkRepository bookmarkRepository;
    PostService postService;
    UserService userService;
    AuthService authService;

    public void bookmarkPost(UUID postId) {
        User user = userService.getRefById(authService.getUserIdFromContext());
        Post post = postService.getRefById(postId);

        if (bookmarkRepository.existsByUser_IdAndPost_Id(postId, authService.getUserIdFromContext())) {
            throw new BookmarkExistsException(BookmarkErrorEnums.BOOKMARK_EXISTS.getMessage());
        }

        bookmarkRepository.save(Bookmark.builder().
                user(user).
                post(post).
                build()
        );
    }

    public void deleteBookmark(UUID postId) {
        User user = userService.getRefById(authService.getUserIdFromContext());
        Post post = postService.getRefById(postId);

        Bookmark bookmark = bookmarkRepository.findByUser_IdAndPost_Id(user.getId(), post.getId())
                .orElseThrow(() -> new BookmarkNotFoundException(BookmarkErrorEnums.BOOKMARK_NOT_FOUND.getMessage()));

        bookmarkRepository.delete(bookmark);
    }

    public boolean isBookmarked(UUID postId) {
        return bookmarkRepository.existsByUser_IdAndPost_Id(authService.getUserIdFromContext(), postId);
    }

    public long getCountBookmarksByPostId(UUID postId) {
        return bookmarkRepository.countAllByPost_Id(postId);
    }

    public List<UUID> getMeBookmarkedIdsByPostId(List<UUID> postIds) {
        UUID currentUserId = authService.getUserIdFromContext();
        return bookmarkRepository.findAllByPost_IdsInAndUser_Id(postIds, currentUserId);
    }

    public Map<UUID, Long> getCountBookmarksByPostIds(List<UUID> idList) {
        List<Object[]> bookmarks = bookmarkRepository.countBookmarkByPostIds(idList);

        Map<UUID, Long> bookmarkMap = new HashMap<>();

        for (Object[] bookmark : bookmarks) {
            bookmarkMap.put((UUID) bookmark[0], (Long) bookmark[1]);
        }

        return bookmarkMap;
    }
}
