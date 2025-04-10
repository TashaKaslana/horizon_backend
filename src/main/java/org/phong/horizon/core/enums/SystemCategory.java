package org.phong.horizon.core.enums;

import lombok.Getter;

@Getter
public enum SystemCategory {
    USER("USER"),
    POST("POST"),
    COMMENT("COMMENT"),
    ASSET("ASSET"),
    SYSTEM("SYSTEM");

    public final String name;
    SystemCategory(String name) {
        this.name = name;
    }
}
