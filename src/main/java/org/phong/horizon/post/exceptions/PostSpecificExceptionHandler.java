package org.phong.horizon.post.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.phong.horizon.core.exception.ApiErrorResponse;
import org.phong.horizon.core.utils.HttpRequestUtils;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@Slf4j
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class PostSpecificExceptionHandler {

    @ExceptionHandler(PostPermissionDenialException.class)
    public ResponseEntity<ApiErrorResponse> handlePostPermissionDenialException(PostPermissionDenialException ex,
                                                                                WebRequest request) {
        log.warn("Post Permission Denied: {}", ex.getMessage());

        ApiErrorResponse apiError = new ApiErrorResponse(
                HttpStatus.FORBIDDEN.value(),
                ex.getMessage(),
                HttpRequestUtils.getRequestPath(request),
                HttpStatus.FORBIDDEN.getReasonPhrase()
        );
        return new ResponseEntity<>(apiError, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(PostNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handlePostNotFoundException(PostNotFoundException ex,
                                                                        WebRequest request) {
        log.warn("Post Not Found: {}", ex.getMessage());

        ApiErrorResponse apiError = new ApiErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                HttpRequestUtils.getRequestPath(request),
                HttpStatus.NOT_FOUND.getReasonPhrase()
        );
        return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(PostInteractionNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handlePostInteractionNotFoundException(PostInteractionNotFoundException ex,
                                                                                   WebRequest request) {
        log.warn("Post Interaction Not Found: {}", ex.getMessage());

        ApiErrorResponse apiError = new ApiErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                HttpRequestUtils.getRequestPath(request),
                HttpStatus.NOT_FOUND.getReasonPhrase()
        );
        return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(PostInteractionAlreadyExistException.class)
    public ResponseEntity<ApiErrorResponse> handlePostInteractionAlreadyExistException(PostInteractionAlreadyExistException ex,
                                                                                       WebRequest request) {
        log.warn("Post Interaction Already Exists: {}", ex.getMessage());

        ApiErrorResponse apiError = new ApiErrorResponse(
                HttpStatus.CONFLICT.value(),
                ex.getMessage(),
                HttpRequestUtils.getRequestPath(request),
                HttpStatus.CONFLICT.getReasonPhrase()
        );
        return new ResponseEntity<>(apiError, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(PostWithAssetAlreadyExistException.class)
    public ResponseEntity<ApiErrorResponse> handlePostAlreadyExistsException(PostWithAssetAlreadyExistException ex,
                                                                             WebRequest request) {
        log.warn("Post With Asset Already Exists: {}", ex.getMessage());

        ApiErrorResponse apiError = new ApiErrorResponse(
                HttpStatus.CONFLICT.value(),
                ex.getMessage(),
                HttpRequestUtils.getRequestPath(request),
                HttpStatus.CONFLICT.getReasonPhrase()
        );
        return new ResponseEntity<>(apiError, HttpStatus.CONFLICT);
    }
}
