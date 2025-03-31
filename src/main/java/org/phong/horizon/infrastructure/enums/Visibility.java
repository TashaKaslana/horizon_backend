package org.phong.horizon.infrastructure.enums;

import lombok.Getter;

@Getter
public enum Visibility {
    PUBLIC("PUBLIC"),
    FRIENDS("FRIENDS"),
    PRIVATE("PRIVATE");

    private final String visibility;

    Visibility(String visibility) {
        this.visibility = visibility;
    }

}
