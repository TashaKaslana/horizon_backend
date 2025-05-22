package org.phong.horizon.post.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.phong.horizon.core.responses.RestApiResponse;
import org.phong.horizon.core.utils.HttpRequestUtils;
import org.phong.horizon.post.subdomain.category.exceptions.PostCategoryExistsException;
import org.phong.horizon.post.subdomain.category.exceptions.PostCategoryNotFoundException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@Slf4j
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class PostSpecificExceptionHandler {

    @ExceptionHandler(PostPermissionDenialException.class)
    public ResponseEntity<RestApiResponse<Void>> handlePostPermissionDenialException(PostPermissionDenialException ex,
                                                                                     WebRequest request) {
        log.debug("Post Permission Denied: {}", ex.getMessage());
        return RestApiResponse.forbidden(HttpRequestUtils.getRequestPath(request), ex.getMessage());
    }

    @ExceptionHandler(PostNotFoundException.class)
    public ResponseEntity<RestApiResponse<Void>> handlePostNotFoundException(PostNotFoundException ex,
                                                                             WebRequest request) {
        log.debug("Post Not Found: {}", ex.getMessage());
        return RestApiResponse.notFound(HttpRequestUtils.getRequestPath(request), ex.getMessage());
    }

    @ExceptionHandler(PostInteractionNotFoundException.class)
    public ResponseEntity<RestApiResponse<Void>> handlePostInteractionNotFoundException(PostInteractionNotFoundException ex,
                                                                                        WebRequest request) {
        log.debug("Post Interaction Not Found: {}", ex.getMessage());
        return RestApiResponse.notFound(HttpRequestUtils.getRequestPath(request), ex.getMessage());
    }

    @ExceptionHandler(PostInteractionAlreadyExistException.class)
    public ResponseEntity<RestApiResponse<Void>> handlePostInteractionAlreadyExistException(PostInteractionAlreadyExistException ex,
                                                                                            WebRequest request) {
        log.debug("Post Interaction Already Exists: {}", ex.getMessage());
        return RestApiResponse.conflict(HttpRequestUtils.getRequestPath(request), ex.getMessage());
    }

    @ExceptionHandler(PostWithAssetAlreadyExistException.class)
    public ResponseEntity<RestApiResponse<Void>> handlePostAlreadyExistsException(PostWithAssetAlreadyExistException ex,
                                                                                  WebRequest request) {
        log.debug("Post With Asset Already Exists: {}", ex.getMessage());
        return RestApiResponse.conflict(HttpRequestUtils.getRequestPath(request), ex.getMessage());
    }

    @ExceptionHandler(PostWithAssetNotFoundException.class)
    public ResponseEntity<RestApiResponse<Void>> handlePostWithAssetNotFoundException(PostWithAssetNotFoundException ex,
                                                                                       WebRequest request) {
        log.debug("Post With Asset Not Found: {}", ex.getMessage());
        return RestApiResponse.notFound(HttpRequestUtils.getRequestPath(request), ex.getMessage());
    }


    @ExceptionHandler(BookmarkExistsException.class)
    public ResponseEntity<RestApiResponse<Void>> handleBookmarkExistsException(BookmarkExistsException ex,
                                                                                 WebRequest request) {
        log.debug("Bookmark Already Exists: {}", ex.getMessage());
        return RestApiResponse.conflict(HttpRequestUtils.getRequestPath(request), ex.getMessage());
    }

    @ExceptionHandler(BookmarkNotFoundException.class)
    public ResponseEntity<RestApiResponse<Void>> handleBookmarkNotFoundException(BookmarkNotFoundException ex,
                                                                                  WebRequest request) {
        log.debug("Bookmark Not Found: {}", ex.getMessage());
        return RestApiResponse.notFound(HttpRequestUtils.getRequestPath(request), ex.getMessage());
    }

    @ExceptionHandler(PostCategoryNotFoundException.class)
    public ResponseEntity<RestApiResponse<Void>> handlePostCategoryNotFoundException(PostCategoryNotFoundException ex,
                                                                                       WebRequest request) {
        log.debug("Post Category Not Found: {}", ex.getMessage());
        return RestApiResponse.notFound(HttpRequestUtils.getRequestPath(request), ex.getMessage());
    }

    @ExceptionHandler(PostCategoryExistsException.class)
    public ResponseEntity<RestApiResponse<Void>> handlePostCategoryAlreadyExistsException(PostCategoryExistsException ex,
                                                                                             WebRequest request) {
        log.debug("Post Category Already Exists: {}", ex.getMessage());
        return RestApiResponse.conflict(HttpRequestUtils.getRequestPath(request), ex.getMessage());
    }
}
