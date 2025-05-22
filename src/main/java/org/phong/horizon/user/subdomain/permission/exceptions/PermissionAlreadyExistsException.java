package org.phong.horizon.user.subdomain.permission.exceptions;

import org.phong.horizon.user.subdomain.permission.enums.PermissionExceptionMessage;

public class PermissionAlreadyExistsException extends PermissionManagementException {
    public PermissionAlreadyExistsException(PermissionExceptionMessage message, Object... args) {
        super(message.format(args));
    }
}

