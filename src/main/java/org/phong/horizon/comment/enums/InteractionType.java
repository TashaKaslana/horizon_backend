package org.phong.horizon.comment.enums;

import lombok.Getter;

@Getter
public enum InteractionType {
    LIKE("like"),
    DISLIKE("dislike");

    private final String type;

    InteractionType(String type) {
        this.type = type;
    }

//    public static InteractionType fromString(String type) {
//        for (InteractionType interactionType : InteractionType.values()) {
//            if (interactionType.type.equalsIgnoreCase(type)) {
//                return interactionType;
//            }
//        }
//        throw new IllegalArgumentException("No enum constant for type: " + type);
//    }
}
