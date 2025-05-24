package org.phong.horizon.admin.logentry.exceptions;

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
public class LogEntryExceptionHandler {

    @ExceptionHandler(LogEntryNotFoundException.class)
    public ResponseEntity<RestApiResponse<Void>> handleLogEntryNotFoundException(
            LogEntryNotFoundException ex, WebRequest request) {
        log.warn("LogEntryNotFoundException: {} for request: {}", ex.getMessage(), HttpRequestUtils.getRequestPath(request));

        return RestApiResponse.notFound(
                HttpRequestUtils.getRequestPath(request),
                ex.getMessage()
        );
    }
}

