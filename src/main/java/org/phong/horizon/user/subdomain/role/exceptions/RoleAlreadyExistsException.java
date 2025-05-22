package org.phong.horizon.user.subdomain.role.exceptions;

import org.phong.horizon.user.subdomain.role.enums.RoleExceptionMessage;

public class RoleAlreadyExistsException extends RoleManagementException {
    public RoleAlreadyExistsException(RoleExceptionMessage message, Object... args) {
        super(message.format(args));
    }
}

