package org.phong.horizon.comment.exceptions;

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
public class CommentSpecificExceptionHandler {

    @ExceptionHandler(CommentInteractionExistException.class)
    public ResponseEntity<ApiErrorResponse> handleCommentInteractionExistException(CommentInteractionExistException ex,
                                                                                   WebRequest request) {
        log.warn("Comment Interaction Already Exists: {}", ex.getMessage());

        ApiErrorResponse apiError = new ApiErrorResponse(
                HttpStatus.CONFLICT.value(),
                ex.getMessage(),
                HttpRequestUtils.getRequestPath(request),
                HttpStatus.CONFLICT.getReasonPhrase()
        );
        return new ResponseEntity<>(apiError, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(CommentInteractionNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleCommentInteractionNotFoundException(CommentInteractionNotFoundException ex,
                                                                                      WebRequest request) {
        log.warn("Comment Interaction Not Found: {}", ex.getMessage());

        ApiErrorResponse apiError = new ApiErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                HttpRequestUtils.getRequestPath(request),
                HttpStatus.NOT_FOUND.getReasonPhrase()
        );
        return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CommentNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleCommentNotFoundException(CommentNotFoundException ex,
                                                                           WebRequest request) {
        log.warn("Comment Not Found: {}", ex.getMessage());

        ApiErrorResponse apiError = new ApiErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                HttpRequestUtils.getRequestPath(request),
                HttpStatus.NOT_FOUND.getReasonPhrase()
        );
        return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
    }
}
