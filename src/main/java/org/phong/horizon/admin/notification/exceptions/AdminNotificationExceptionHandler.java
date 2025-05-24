package org.phong.horizon.admin.notification.exceptions;

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
public class AdminNotificationExceptionHandler {

    @ExceptionHandler(NotificationNotFoundException.class)
    public ResponseEntity<RestApiResponse<Void>> handleNotificationNotFoundException(
            NotificationNotFoundException ex, WebRequest request) {
        log.warn("NotificationNotFoundException: {} for request: {}", ex.getMessage(), HttpRequestUtils.getRequestPath(request));

        return RestApiResponse.notFound(
                HttpRequestUtils.getRequestPath(request),
                ex.getMessage()
        );
    }
}

