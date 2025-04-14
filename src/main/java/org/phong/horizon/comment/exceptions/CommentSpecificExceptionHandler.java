package org.phong.horizon.comment.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.phong.horizon.core.responses.RestApiResponse;
import org.phong.horizon.core.utils.HttpRequestUtils;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@Slf4j
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CommentSpecificExceptionHandler {

    @ExceptionHandler(CommentInteractionExistException.class)
    public ResponseEntity<RestApiResponse<Void>> handleCommentInteractionExistException(CommentInteractionExistException ex,
                                                                                       WebRequest request) {
        log.warn("Comment Interaction Already Exists: {}", ex.getMessage());
        return RestApiResponse.conflict(HttpRequestUtils.getRequestPath(request), ex.getMessage());
    }

    @ExceptionHandler(CommentInteractionNotFoundException.class)
    public ResponseEntity<RestApiResponse<Void>> handleCommentInteractionNotFoundException(CommentInteractionNotFoundException ex,
                                                                                          WebRequest request) {
        log.warn("Comment Interaction Not Found: {}", ex.getMessage());
        return RestApiResponse.notFound(HttpRequestUtils.getRequestPath(request), ex.getMessage());
    }

    @ExceptionHandler(CommentNotFoundException.class)
    public ResponseEntity<RestApiResponse<Void>> handleCommentNotFoundException(CommentNotFoundException ex,
                                                                               WebRequest request) {
        log.warn("Comment Not Found: {}", ex.getMessage());
        return RestApiResponse.notFound(HttpRequestUtils.getRequestPath(request), ex.getMessage());
    }
}
