package org.phong.horizon.user.subdomain.role.exceptions;

import org.phong.horizon.user.subdomain.role.enums.RoleExceptionMessage;

public class PermissionNotFoundForRoleException extends RoleManagementException {
    public PermissionNotFoundForRoleException(RoleExceptionMessage message, Object... args) {
        super(message.format(args));
    }
}

