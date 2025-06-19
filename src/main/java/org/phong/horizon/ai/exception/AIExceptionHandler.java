package org.phong.horizon.ai.exception;

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
public class AIExceptionHandler {
     @ExceptionHandler(AICommunicationException.class)
     public ResponseEntity<RestApiResponse<Void>> handleAICommunicationException(AICommunicationException ex, WebRequest request) {
         log.error("AI Communication Error: {}", ex.getMessage());
         return RestApiResponse.internalServerError(HttpRequestUtils.getRequestPath(request), ex.getMessage());
     }
}
