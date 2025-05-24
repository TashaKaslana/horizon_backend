package org.phong.horizon.report.exceptions;

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
public class ReportExceptionHandler {

    @ExceptionHandler(ReportNotFoundException.class)
    public ResponseEntity<RestApiResponse<Void>> handleReportNotFoundException(
            ReportNotFoundException ex, WebRequest request) {
        log.warn("ReportNotFoundException: {} for request: {}", ex.getMessage(), HttpRequestUtils.getRequestPath(request));
        return RestApiResponse.notFound(
                HttpRequestUtils.getRequestPath(request),
                ex.getMessage()
        );
    }

    @ExceptionHandler(InvalidReportInputException.class)
    public ResponseEntity<RestApiResponse<Void>> handleInvalidReportInputException(
            InvalidReportInputException ex, WebRequest request) {
        log.warn("InvalidReportInputException: {} for request: {}", ex.getMessage(), HttpRequestUtils.getRequestPath(request));

        return RestApiResponse.badRequest(
                HttpRequestUtils.getRequestPath(request),
                ex.getMessage()
        );
    }
}

