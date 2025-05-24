package org.phong.horizon.user.subdomain.permission.exceptions;

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
public class PermissionExceptionHandler {

    @ExceptionHandler(PermissionNotFoundException.class)
    public ResponseEntity<RestApiResponse<Void>> handlePermissionNotFoundException(
            PermissionNotFoundException ex, WebRequest request) {
        log.warn("PermissionNotFoundException: {} for request: {}", ex.getMessage(), HttpRequestUtils.getRequestPath(request));
        return RestApiResponse.notFound(
                HttpRequestUtils.getRequestPath(request),
                ex.getMessage()
        );
    }

    @ExceptionHandler(PermissionAlreadyExistsException.class)
    public ResponseEntity<RestApiResponse<Void>> handlePermissionAlreadyExistsException(
            PermissionAlreadyExistsException ex, WebRequest request) {
        log.warn("PermissionAlreadyExistsException: {} for request: {}", ex.getMessage(), HttpRequestUtils.getRequestPath(request));
        return RestApiResponse.conflict(
                HttpRequestUtils.getRequestPath(request),
                ex.getMessage()
        );
    }

    @ExceptionHandler(PermissionManagementException.class)
    public ResponseEntity<RestApiResponse<Void>> handlePermissionManagementException(
            PermissionManagementException ex, WebRequest request) {

        log.warn("PermissionManagementException: {} for request: {}", ex.getMessage(), HttpRequestUtils.getRequestPath(request));
        if (ex.getCause() instanceof org.springframework.dao.DataIntegrityViolationException) {
            return RestApiResponse.conflict(
                HttpRequestUtils.getRequestPath(request),
                ex.getMessage()
            );
        }
        return RestApiResponse.badRequest(
                HttpRequestUtils.getRequestPath(request),
                ex.getMessage()
        );
    }
}

