package org.phong.horizon.infrastructure.enums;

import lombok.Getter;

@Getter
public enum Role {
    PUBLIC("PUBLIC"), USER("USER"), ADMIN("ADMIN");

    private final String role;

    Role(String role) {
        this.role = role;
    }
}
