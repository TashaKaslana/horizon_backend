package org.phong.horizon.user.subdomain.role.exceptions;

public class RoleManagementException extends RuntimeException {
    public RoleManagementException(String message) {
        super(message);
    }

    public RoleManagementException(String message, Throwable cause) {
        super(message, cause);
    }
}

