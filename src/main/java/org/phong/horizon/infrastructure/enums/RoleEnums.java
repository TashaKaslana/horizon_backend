package org.phong.horizon.infrastructure.enums;

import lombok.Getter;

@Getter
public enum RoleEnums {
    PUBLIC("PUBLIC"), USER("USER"), ADMIN("ADMIN");

    private final String role;

    RoleEnums(String role) {
        this.role = role;
    }
}
