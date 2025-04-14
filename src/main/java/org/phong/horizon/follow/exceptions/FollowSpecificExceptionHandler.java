package org.phong.horizon.follow.exceptions;

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
public class FollowSpecificExceptionHandler {

    @ExceptionHandler(FollowNotFoundException.class)
    public ResponseEntity<RestApiResponse<Void>> handleFollowNotFoundException(FollowNotFoundException ex,
                                                                               WebRequest request) {
        log.warn("Follow Not Found: {}", ex.getMessage());
        return RestApiResponse.notFound(HttpRequestUtils.getRequestPath(request), ex.getMessage());
    }

    @ExceptionHandler(FollowSelfException.class)
    public ResponseEntity<RestApiResponse<Void>> handleFollowSelfException(FollowSelfException ex,
                                                                           WebRequest request) {
        log.warn("Follow Self Error: {}", ex.getMessage());
        return RestApiResponse.badRequest(HttpRequestUtils.getRequestPath(request), ex.getMessage());
    }
}
