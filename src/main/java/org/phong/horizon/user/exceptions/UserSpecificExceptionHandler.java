package org.phong.horizon.user.exceptions;

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
public class UserSpecificExceptionHandler {
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<RestApiResponse<Void>> handleUserNotFoundException(
            UserNotFoundException ex, WebRequest request) {

        log.warn("User Not Found: {}", ex.getMessage());

        return RestApiResponse.notFound(
                HttpRequestUtils.getRequestPath(request),
                ex.getMessage()
        );
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<RestApiResponse<Void>> handleUserAlreadyExistsException(
            UserAlreadyExistsException ex, WebRequest request) {

        log.warn("User Already Exists: {}", ex.getMessage());

        return RestApiResponse.conflict(
                HttpRequestUtils.getRequestPath(request),
                ex.getMessage()
        );
    }
}
