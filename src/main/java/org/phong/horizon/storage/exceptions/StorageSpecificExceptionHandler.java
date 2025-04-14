package org.phong.horizon.storage.exceptions;

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
public class StorageSpecificExceptionHandler {
    @ExceptionHandler(AssetAlreadyExistsException.class)
    public ResponseEntity<RestApiResponse<Void>> handleAssetAlreadyExistsException(AssetAlreadyExistsException ex,
                                                                                   WebRequest request) {
        log.warn("Asset Already Exists: {}", ex.getMessage());
        return RestApiResponse.conflict(HttpRequestUtils.getRequestPath(request), ex.getMessage());
    }

    @ExceptionHandler(AssetNotFoundException.class)
    public ResponseEntity<RestApiResponse<Void>> handleAssetNotFoundException(AssetNotFoundException ex,
                                                                              WebRequest request) {
        log.warn("Asset Not Found: {}", ex.getMessage());
        return RestApiResponse.notFound(HttpRequestUtils.getRequestPath(request), ex.getMessage());
    }

    @ExceptionHandler(CloudinaryException.class)
    public ResponseEntity<RestApiResponse<Void>> handleCloudinaryException(CloudinaryException ex,
                                                                           WebRequest request) {
        log.warn("Cloudinary Error: {}", ex.getMessage());
        return RestApiResponse.internalServerError(HttpRequestUtils.getRequestPath(request), ex.getMessage());
    }

    @ExceptionHandler(MissingAssetPropertiesException.class)
    public ResponseEntity<RestApiResponse<Void>> handleMissingAssetPropertiesException(MissingAssetPropertiesException ex,
                                                                                       WebRequest request) {
        log.warn("Missing Asset Properties: {}", ex.getMessage());
        return RestApiResponse.badRequest(HttpRequestUtils.getRequestPath(request), ex.getMessage());
    }
}
