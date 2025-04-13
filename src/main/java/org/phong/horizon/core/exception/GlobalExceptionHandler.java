package org.phong.horizon.core.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.phong.horizon.core.enums.SystemError;
import org.phong.horizon.core.utils.HttpRequestUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.lang.NonNull;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status, @NonNull WebRequest request) {

        log.warn("Validation Error: {}", ex.getMessage());

        Map<String, String> fieldErrors = new HashMap<>();
        List<String> globalErrors = ex.getBindingResult()
                .getGlobalErrors().stream()
                .map(ObjectError::getDefaultMessage)
                .collect(Collectors.toList());

        ex.getBindingResult().getFieldErrors().forEach(error ->
                fieldErrors.put(error.getField(), error.getDefaultMessage())
        );

        ApiErrorResponse apiError = new ApiErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                SystemError.VALIDATION_FAILED_MSG,
                HttpRequestUtils.getRequestPath(request),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                fieldErrors,
                globalErrors.isEmpty() ? null : globalErrors
        );

        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex, @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status, @NonNull WebRequest request) {

        log.warn("Malformed JSON request: {}", ex.getMessage());
        ApiErrorResponse apiError = new ApiErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                SystemError.MALFORMED_REQUEST_MSG,
                HttpRequestUtils.getRequestPath(request),
                HttpStatus.BAD_REQUEST.getReasonPhrase()
        );
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
            HttpRequestMethodNotSupportedException ex, @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status, @NonNull WebRequest request) {

        log.warn("Method Not Supported: {}", ex.getMessage());
        ApiErrorResponse apiError = new ApiErrorResponse(
                HttpStatus.METHOD_NOT_ALLOWED.value(),
                SystemError.METHOD_NOT_SUPPORTED_MSG + ". Supported methods: " + ex.getSupportedHttpMethods(),
                HttpRequestUtils.getRequestPath(request),
                HttpStatus.METHOD_NOT_ALLOWED.getReasonPhrase()
        );
        return new ResponseEntity<>(apiError, headers, HttpStatus.METHOD_NOT_ALLOWED);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex, @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status, @NonNull WebRequest request) {

        log.warn("Missing Request Parameter: {}", ex.getParameterName());
        String message = SystemError.MISSING_PARAMETER_MSG.getErrorMessage() + ": " + ex.getParameterName() + " (" + ex.getParameterType() + ")";
        ApiErrorResponse apiError = new ApiErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                message,
                HttpRequestUtils.getRequestPath(request),
                HttpStatus.BAD_REQUEST.getReasonPhrase()
        );
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleConstraintViolationException(
            ConstraintViolationException ex, WebRequest request) {

        log.warn("Constraint Violation: {}", ex.getMessage());

        Map<String, String> constraintErrors = ex.getConstraintViolations().stream()
                .collect(Collectors.toMap(
                        violation -> violation.getPropertyPath().toString(),
                        ConstraintViolation::getMessage
                ));

        ApiErrorResponse apiError = new ApiErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                SystemError.VALIDATION_FAILED_MSG,
                HttpRequestUtils.getRequestPath(request),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                constraintErrors,
                null
        );
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    // --- Generic Fallback Handler (Catches anything else) ---
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGlobalException(
            Exception ex, WebRequest request) {

        log.error("Unhandled exception occurred: {}", ex.getMessage(), ex);

        ApiErrorResponse apiError = new ApiErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                SystemError.GENERIC_ERROR_MSG,
                HttpRequestUtils.getRequestPath(request),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase()
        );
        return new ResponseEntity<>(apiError, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}