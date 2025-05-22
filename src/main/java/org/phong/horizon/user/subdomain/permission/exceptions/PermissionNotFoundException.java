package org.phong.horizon.user.subdomain.permission.exceptions;

import org.phong.horizon.user.subdomain.permission.enums.PermissionExceptionMessage;

public class PermissionNotFoundException extends PermissionManagementException {
    public PermissionNotFoundException(PermissionExceptionMessage message, Object... args) {
        super(message.format(args));
    }
}

