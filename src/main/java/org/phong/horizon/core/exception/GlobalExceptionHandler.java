package org.phong.horizon.core.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.phong.horizon.core.enums.SystemError;
import org.phong.horizon.core.responses.RestApiResponse;
import org.phong.horizon.core.utils.HttpRequestUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<RestApiResponse<Void>> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                              @NonNull WebRequest request) {
        log.warn("Validation Error: {}", ex.getMessage());

        Map<String, String> fieldErrors = new HashMap<>();
        List<String> globalErrors = ex.getBindingResult()
                .getGlobalErrors().stream()
                .map(ObjectError::getDefaultMessage)
                .collect(Collectors.toList());

        ex.getBindingResult().getFieldErrors().forEach(error ->
                fieldErrors.put(error.getField(), error.getDefaultMessage())
        );

        return RestApiResponse.validationError(
                HttpRequestUtils.getRequestPath(request),
                SystemError.VALIDATION_FAILED_MSG.getErrorMessage(),
                fieldErrors,
                globalErrors.isEmpty() ? null : globalErrors
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<RestApiResponse<Void>> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
                                                                              @NonNull WebRequest request) {
        log.warn("Malformed JSON request: {}", ex.getMessage());

        return RestApiResponse.badRequest(HttpRequestUtils.getRequestPath(request), SystemError.MALFORMED_REQUEST_MSG.getErrorMessage());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<RestApiResponse<Void>> handleHttpRequestMethodNotSupported(
            HttpRequestMethodNotSupportedException ex,
            @NonNull WebRequest request) {
        String message = SystemError.METHOD_NOT_SUPPORTED_MSG + ". Supported methods: " + ex.getSupportedHttpMethods();
        log.warn("Method Not Supported: {}", ex.getMessage());

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setAllow(Objects.requireNonNull(ex.getSupportedHttpMethods()));

        ApiErrorResponse errorResponse = new ApiErrorResponse(
                HttpStatus.METHOD_NOT_ALLOWED.value(),
                message,
                HttpRequestUtils.getRequestPath(request),
                HttpStatus.METHOD_NOT_ALLOWED.getReasonPhrase(),
                null,
                null
        );

        return RestApiResponse.methodNotAllowed(errorResponse, SystemError.METHOD_NOT_SUPPORTED_MSG.toString(), responseHeaders);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<RestApiResponse<Void>> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex,
            @NonNull WebRequest request) {
        log.warn("Missing Request Parameter: {}", ex.getParameterName());
        String message = SystemError.MISSING_PARAMETER_MSG.getErrorMessage() + ": " + ex.getParameterName() + " (" + ex.getParameterType() + ")";

        ApiErrorResponse apiError = new ApiErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                message,
                HttpRequestUtils.getRequestPath(request),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                null,
                null
        );

        return RestApiResponse.badRequest(apiError, SystemError.MISSING_PARAMETER_MSG.getErrorMessage());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<RestApiResponse<Void>> handleConstraintViolationException(
            ConstraintViolationException ex,
            WebRequest request) {
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
        return RestApiResponse.badRequest(apiError);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<RestApiResponse<Void>> handleDataIntegrityViolationException(
            DataIntegrityViolationException ex, WebRequest request) {
        log.warn("Data Integrity Violation: {}", ex.getMessage());

        if (ex.getRootCause() != null) {
            log.warn("Root cause: {}", ex.getRootCause().getMessage());
        }

        return RestApiResponse.conflict(
                HttpRequestUtils.getRequestPath(request),
                SystemError.DATA_INTEGRITY_VIOLATION.getErrorMessage()
        );
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<RestApiResponse<Void>> handleMethodArgumentTypeMismatch(
            MethodArgumentTypeMismatchException ex,
            @NonNull WebRequest request) {
        log.warn("Method Argument Type Mismatch: {}", ex.getMessage());

        String message = SystemError.INVALID_ARGUMENT_TYPE_MSG.getErrorMessage() + ": " + ex.getName()
                + " (" + Objects.requireNonNull(ex.getRequiredType()).getSimpleName() + "). "
                + "Getting value type : " + Objects.requireNonNull(ex.getValue()).getClass().getSimpleName();

        ApiErrorResponse apiError = new ApiErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                message,
                HttpRequestUtils.getRequestPath(request),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                null,
                null
        );

        return RestApiResponse.badRequest(apiError, SystemError.INVALID_ARGUMENT_TYPE_MSG.getErrorMessage());
    }

    // --- Generic Fallback Handler (Catches anything else) ---
    @ExceptionHandler(Exception.class)
    public ResponseEntity<RestApiResponse<Void>> handleGlobalException(
            Exception ex, WebRequest request) {
        log.error("Unhandled exception occurred: {}", ex.getMessage(), ex);

        return RestApiResponse.internalServerError(HttpRequestUtils.getRequestPath(request), SystemError.GENERIC_ERROR_MSG.getErrorMessage());
    }
}