package org.phong.horizon.historyactivity.exceptions;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.phong.horizon.core.responses.RestApiResponse;
import org.phong.horizon.core.utils.HttpRequestUtils;
import org.phong.horizon.historyactivity.enums.HistoryActivityBusinessError;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@AllArgsConstructor
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class ActivitySpecificExceptionHandler {
    @ExceptionHandler(ActivityTypeNotFoundException.class)
    public ResponseEntity<RestApiResponse<Void>> handleActivityTypeNotFound(ActivityTypeNotFoundException ex,
                                                                            @NonNull WebRequest request) {
        log.warn("Activity Type Not Found: {}", ex.getMessage());

        return RestApiResponse.notFound(
                HttpRequestUtils.getRequestPath(request),
                HistoryActivityBusinessError.ACTIVITY_TYPE_CODE_NOT_FOUND.getMessage()
        );
    }
}
