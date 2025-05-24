package org.phong.horizon.post.subdomain.category.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.phong.horizon.core.responses.RestApiResponse;
import org.phong.horizon.core.utils.HttpRequestUtils;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
public class PostCategoryExceptionHandler {

    @ExceptionHandler(PostCategoryNotFoundException.class)
    public ResponseEntity<RestApiResponse<Void>> handlePostCategoryNotFoundException(
            PostCategoryNotFoundException ex, WebRequest request) {
        log.warn("PostCategoryNotFoundException: {} for request: {}", ex.getMessage(), HttpRequestUtils.getRequestPath(request));
        return RestApiResponse.notFound(
                HttpRequestUtils.getRequestPath(request),
                ex.getMessage()
        );
    }

    @ExceptionHandler(PostCategoryExistsException.class)
    public ResponseEntity<RestApiResponse<Void>> handlePostCategoryExistsException(
            PostCategoryExistsException ex, WebRequest request) {
        log.warn("PostCategoryExistsException: {} for request: {}", ex.getMessage(), HttpRequestUtils.getRequestPath(request));
        return RestApiResponse.conflict(
                HttpRequestUtils.getRequestPath(request),
                ex.getMessage()
        );
    }
}

