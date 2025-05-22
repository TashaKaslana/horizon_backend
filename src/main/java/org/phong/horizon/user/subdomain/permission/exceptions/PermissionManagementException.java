package org.phong.horizon.user.subdomain.permission.exceptions;

public class PermissionManagementException extends RuntimeException {
    public PermissionManagementException(String message) {
        super(message);
    }

    public PermissionManagementException(String message, Throwable cause) {
        super(message, cause);
    }
}

