package org.phong.horizon.historyactivity.dtos;

public record ActivityPart(
        String type,
        String value,
        String entity,
        String id,
        String label
) {
    public static ActivityPart text(String value) {
        return new ActivityPart("text", value, null, null, null);
    }

    public static ActivityPart object(String entity, String id, String label) {
        return new ActivityPart("object", null, entity, id, label);
    }
}


