package org.phong.horizon.core.responses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Getter;
import org.phong.horizon.core.exception.ApiErrorResponse;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Getter
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class RestApiResponse<T> {

    private final boolean success;
    private final String message;
    private final T data;
    private final ApiErrorResponse error;
    private final LocalDateTime timestamp;
    private final ResponseMetadata metadata;

    // Constructor for success with data
    private RestApiResponse(T data, String message) {
        this.success = true;
        this.message = message;
        this.data = data;
        this.error = null;
        this.metadata = null;
        this.timestamp = LocalDateTime.now();
    }

    // Constructor for success with data and metadata
    private RestApiResponse(T data, String message, ResponseMetadata metadata) {
        this.success = true;
        this.message = message;
        this.data = data;
        this.error = null;
        this.metadata = metadata;
        this.timestamp = LocalDateTime.now();
    }

    // Constructor for success without data (e.g., for 200 OK on DELETE/PUT)
    private RestApiResponse(String message) {
        this.success = true;
        this.message = message;
        this.data = null;
        this.error = null;
        this.metadata = null;
        this.timestamp = LocalDateTime.now();
    }

    // Constructor for error (takes the detailed ApiErrorResponse)
    private RestApiResponse(ApiErrorResponse error, String topLevelMessage) {
        this.success = false;
        this.message = (topLevelMessage != null) ? topLevelMessage : (error != null ? error.getMessage() : "ERROR");
        this.data = null;
        this.error = error;
        this.metadata = null;
        this.timestamp = (error != null && error.getTimestamp() != null) ? error.getTimestamp() : LocalDateTime.now();
    }


    // --- Static Factory Methods ---
    // ----- Success Factories -----
    public static <E> ResponseEntity<RestApiResponse<List<E>>> success(Page<E> page, String message) {
        return ResponseEntity.ok(new RestApiResponse<>(page.getContent(), message, new ResponseMetadata(page)));
    }

    public static <E> ResponseEntity<RestApiResponse<List<E>>> success(Page<E> page) {
        return ResponseEntity.ok(new RestApiResponse<>(page.getContent(), "SUCCESS", new ResponseMetadata(page)));
    }

    public static <T> ResponseEntity<RestApiResponse<T>> success(T data, String message) {
        return ResponseEntity.ok(new RestApiResponse<>(data, message));
    }

    public static <T> ResponseEntity<RestApiResponse<T>> success(T data) {
        return ResponseEntity.ok(new RestApiResponse<>(data, "SUCCESS"));
    }

    public static ResponseEntity<RestApiResponse<String>> success(String message) {
        return ResponseEntity.ok(new RestApiResponse<>(message));
    }

    public static ResponseEntity<RestApiResponse<Void>> success() {
        return ResponseEntity.ok(new RestApiResponse<>("SUCCESS"));
    }

    // Factory for 201 Created
    public static <T> ResponseEntity<RestApiResponse<T>> created(T data, String message) {
        return ResponseEntity.status(HttpStatus.CREATED).body(new RestApiResponse<>(data, message));
    }

    public static <T> ResponseEntity<RestApiResponse<T>> created(T data) {
        return created(data, "CREATED");
    }

    public static ResponseEntity<RestApiResponse<Void>> created() {
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    public static ResponseEntity<RestApiResponse<Void>> noContent() {
        return ResponseEntity.noContent().build(); //204 No Content
    }

    // --- Error Factories (Now wrap ApiErrorResponse) ---

    // Helper to create ApiErrorResponse and wrap it
    private static <E> ResponseEntity<RestApiResponse<E>> buildErrorResponse(ApiErrorResponse errorDetails, HttpStatus status) {
        // Pass null for topLevelMessage to use errorDetails.getMessage()
        RestApiResponse<E> body = new RestApiResponse<>(errorDetails, null);
        return ResponseEntity.status(status).body(body);
    }

    public static ResponseEntity<RestApiResponse<Void>> badRequest(String path, String message) {
        ApiErrorResponse details = new ApiErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                message,
                path,
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                null,
                null
        );
        return buildErrorResponse(details, HttpStatus.BAD_REQUEST);
    }

    public static ResponseEntity<RestApiResponse<Void>> badRequest(String path, String message, String errorDescription) {
        ApiErrorResponse details = new ApiErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                message,
                path,
                errorDescription
        );
        return buildErrorResponse(details, HttpStatus.BAD_REQUEST);
    }

    public static ResponseEntity<RestApiResponse<Void>> notFound(String path, String message) {
        ApiErrorResponse details = new ApiErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                message,
                path,
                HttpStatus.NOT_FOUND.getReasonPhrase()
        );
        return buildErrorResponse(details, HttpStatus.NOT_FOUND);


    }

    public static ResponseEntity<RestApiResponse<Void>> conflict(String path, String message) {
        ApiErrorResponse details = new ApiErrorResponse(
                HttpStatus.CONFLICT.value(),
                message,
                path,
                HttpStatus.CONFLICT.getReasonPhrase()
        );
        return buildErrorResponse(details, HttpStatus.CONFLICT);
    }

    public static ResponseEntity<RestApiResponse<Void>> internalServerError(String path, String message) {
        ApiErrorResponse details = new ApiErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                message,
                path,
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase()
        );
        return buildErrorResponse(details, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public static ResponseEntity<RestApiResponse<Void>> internalServerError(String path, String message, String errorDescription) {
        ApiErrorResponse details = new ApiErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                message,
                path,
                errorDescription
        );
        return buildErrorResponse(details, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Special case: Validation errors need fieldErrors/globalErrors
    public static ResponseEntity<RestApiResponse<Void>> validationError(String path, String message, Map<String, String> fieldErrors, List<String> globalErrors) {
        ApiErrorResponse details = new ApiErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                message,
                path,
                "Validation Failed",
                fieldErrors,
                globalErrors
        );
        return buildErrorResponse(details, HttpStatus.BAD_REQUEST);
    }
}