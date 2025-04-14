package org.phong.horizon.notification.exceptions;

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
public class NotificationSpecificExceptionHandler {

    @ExceptionHandler(NotificationAccessDenialException.class)
    public ResponseEntity<RestApiResponse<Void>> handleNotificationAccessDenialException(NotificationAccessDenialException ex,
                                                                                         WebRequest request) {
        log.warn("Notification Access Denied: {}", ex.getMessage());
        return RestApiResponse.forbidden(HttpRequestUtils.getRequestPath(request), ex.getMessage());
    }

    @ExceptionHandler(NotificationsNotFoundException.class)
    public ResponseEntity<RestApiResponse<Void>> handleNotificationsNotFoundException(NotificationsNotFoundException ex,
                                                                                      WebRequest request) {
        log.warn("Notifications Not Found: {}", ex.getMessage());
        return RestApiResponse.notFound(HttpRequestUtils.getRequestPath(request), ex.getMessage());
    }
}
