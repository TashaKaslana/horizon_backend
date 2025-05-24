package org.phong.horizon.user.subdomain.role.exceptions;

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
public class RoleExceptionHandler {

    @ExceptionHandler(RoleNotFoundException.class)
    public ResponseEntity<RestApiResponse<Void>> handleRoleNotFoundException(
            RoleNotFoundException ex, WebRequest request) {
        log.warn("RoleNotFoundException: {} for request: {}", ex.getMessage(), HttpRequestUtils.getRequestPath(request));
        return RestApiResponse.notFound(
                HttpRequestUtils.getRequestPath(request),
                ex.getMessage()
        );
    }

    @ExceptionHandler(RoleAlreadyExistsException.class)
    public ResponseEntity<RestApiResponse<Void>> handleRoleAlreadyExistsException(
            RoleAlreadyExistsException ex, WebRequest request) {
        log.warn("RoleAlreadyExistsException: {} for request: {}", ex.getMessage(), HttpRequestUtils.getRequestPath(request));
        return RestApiResponse.conflict(
                HttpRequestUtils.getRequestPath(request),
                ex.getMessage()
        );
    }

    @ExceptionHandler(PermissionNotFoundForRoleException.class)
    public ResponseEntity<RestApiResponse<Void>> handlePermissionNotFoundForRoleException(
            PermissionNotFoundForRoleException ex, WebRequest request) {

        log.warn("PermissionNotFoundForRoleException: {} for request: {}", ex.getMessage(), HttpRequestUtils.getRequestPath(request));
        return RestApiResponse.badRequest(
                HttpRequestUtils.getRequestPath(request),
                ex.getMessage()
        );
    }

    @ExceptionHandler(RoleManagementException.class)
    public ResponseEntity<RestApiResponse<Void>> handleRoleManagementException(
            RoleManagementException ex, WebRequest request) {

        log.warn("RoleManagementException: {} for request: {}", ex.getMessage(), HttpRequestUtils.getRequestPath(request));
        if (ex.getMessage().contains("Cannot delete role")) {
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

