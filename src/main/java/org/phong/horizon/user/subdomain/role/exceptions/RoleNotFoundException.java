package org.phong.horizon.user.subdomain.role.exceptions;

import org.phong.horizon.user.subdomain.role.enums.RoleExceptionMessage;

public class RoleNotFoundException extends RoleManagementException {
    public RoleNotFoundException(RoleExceptionMessage message, Object... args) {
        super(message.format(args));
    }
}

