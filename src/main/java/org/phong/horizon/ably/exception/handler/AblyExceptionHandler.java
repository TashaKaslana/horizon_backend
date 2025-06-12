package org.phong.horizon.ably.exception.handler;

import org.phong.horizon.ably.exception.AblyPublishException;
import org.phong.horizon.core.responses.RestApiResponse;
import org.phong.horizon.core.utils.HttpRequestUtils;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class AblyExceptionHandler {

    @ExceptionHandler(AblyPublishException.class)
    public ResponseEntity<RestApiResponse<Void>> handleAblyPublishException(AblyPublishException ex, WebRequest request) {
        System.err.println("Ably Publish Exception: " + ex.getMessage() + ", Cause: " + (ex.getCause() != null ? ex.getCause().getMessage() : "N/A"));

        String path = "unavailable (async operation)";
        if (request != null) {
            try {
                path = HttpRequestUtils.getRequestPath(request);
            } catch (IllegalStateException e) {
                System.err.println("Could not get request path: " + e.getMessage());
            } catch (Exception e) {
                System.err.println("Unexpected error retrieving request path: " + e.getMessage());
                path = "error retrieving path";
            }
        }

        return RestApiResponse.internalServerError(path, "Failed to publish message to Ably. Please try again later.");
    }
}
