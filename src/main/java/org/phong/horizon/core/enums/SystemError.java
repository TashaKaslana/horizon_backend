package org.phong.horizon.core.enums;


import lombok.Getter;

@Getter
public enum SystemError {
    VALIDATION_FAILED_MSG("Validation failed"),
    MALFORMED_REQUEST_MSG("Malformed JSON request"),
    METHOD_NOT_SUPPORTED_MSG("Method not supported"),
    GENERIC_ERROR_MSG("An unexpected error occurred"),
    MISSING_PARAMETER_MSG("Required parameter is missing");

    private final String errorMessage;

    SystemError(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
