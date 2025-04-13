package org.phong.horizon.notification.exceptions;

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
public class NotificationSpecificExceptionHandler {

    @ExceptionHandler(NotificationAccessDenialException.class)
    public ResponseEntity<ApiErrorResponse> handleNotificationAccessDenialException(NotificationAccessDenialException ex,
                                                                                    WebRequest request) {
        log.warn("Notification Access Denied: {}", ex.getMessage());

        ApiErrorResponse apiError = new ApiErrorResponse(
                HttpStatus.FORBIDDEN.value(),
                ex.getMessage(),
                HttpRequestUtils.getRequestPath(request),
                HttpStatus.FORBIDDEN.getReasonPhrase()
        );
        return new ResponseEntity<>(apiError, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(NotificationsNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNotificationsNotFoundException(NotificationsNotFoundException ex,
                                                                                 WebRequest request) {
        log.warn("Notifications Not Found: {}", ex.getMessage());

        ApiErrorResponse apiError = new ApiErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                HttpRequestUtils.getRequestPath(request),
                HttpStatus.NOT_FOUND.getReasonPhrase()
        );
        return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
    }
}
