package org.phong.horizon.storage.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.phong.horizon.core.exception.ApiErrorResponse;
import org.phong.horizon.core.utils.HttpRequestUtils;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@Slf4j
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class StorageSpecificExceptionHandler {
    @ExceptionHandler(AssetAlreadyExistsException.class)
    public ResponseEntity<ApiErrorResponse> handleAssetAlreadyExistsException(AssetAlreadyExistsException ex,
                                                                              WebRequest request) {
        log.warn("Asset Already Exists: {}", ex.getMessage());

        ApiErrorResponse apiError = new ApiErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                HttpRequestUtils.getRequestPath(request),
                HttpStatus.CONFLICT.getReasonPhrase()
        );
        return new ResponseEntity<>(apiError, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(AssetNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleAssetNotFoundException(AssetNotFoundException ex,
                                                                         WebRequest request) {
        log.warn("Asset Not Found: {}", ex.getMessage());

        ApiErrorResponse apiError = new ApiErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                HttpRequestUtils.getRequestPath(request),
                HttpStatus.NOT_FOUND.getReasonPhrase()
        );
        return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CloudinaryException.class)
    public ResponseEntity<ApiErrorResponse> handleCloudinaryException(CloudinaryException ex,
                                                                      WebRequest request) {
        log.warn("Cloudinary Error: {}", ex.getMessage());

        ApiErrorResponse apiError = new ApiErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                ex.getMessage(),
                HttpRequestUtils.getRequestPath(request),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase()
        );
        return new ResponseEntity<>(apiError, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MissingAssetPropertiesException.class)
    public ResponseEntity<ApiErrorResponse> handleMissingAssetPropertiesException(MissingAssetPropertiesException ex,
                                                                                  WebRequest request) {
        log.warn("Missing Asset Properties: {}", ex.getMessage());

        ApiErrorResponse apiError = new ApiErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                HttpRequestUtils.getRequestPath(request),
                HttpStatus.BAD_REQUEST.getReasonPhrase()
        );
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }
}
