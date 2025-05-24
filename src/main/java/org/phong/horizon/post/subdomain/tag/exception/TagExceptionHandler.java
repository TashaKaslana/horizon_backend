package org.phong.horizon.post.subdomain.tag.exception;

import lombok.extern.slf4j.Slf4j;
import org.phong.horizon.core.responses.RestApiResponse;
import org.phong.horizon.core.utils.HttpRequestUtils;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
public class TagExceptionHandler {

    @ExceptionHandler(TagNotFoundException.class)
    public ResponseEntity<RestApiResponse<Void>> handleTagNotFoundException(
            TagNotFoundException ex, WebRequest request) {
        log.warn("TagNotFoundException: {} for request: {}", ex.getMessage(), HttpRequestUtils.getRequestPath(request));
        return RestApiResponse.notFound(
                HttpRequestUtils.getRequestPath(request),
                ex.getMessage()
        );
    }

    @ExceptionHandler(TagPropertyConflictException.class)
    public ResponseEntity<RestApiResponse<Void>> handleTagPropertyConflictException(
            TagPropertyConflictException ex, WebRequest request) {
        log.warn("TagPropertyConflictException: {} for request: {}", ex.getMessage(), HttpRequestUtils.getRequestPath(request));
        return RestApiResponse.conflict(
                HttpRequestUtils.getRequestPath(request),
                ex.getMessage()
        );
    }
}

