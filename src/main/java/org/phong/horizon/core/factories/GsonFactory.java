package org.phong.horizon.core.factories;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.phong.horizon.core.adaptors.LocalDateTimeAdapter;
import org.phong.horizon.core.adaptors.OffsetDateTimeAdapter;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

public class GsonFactory {

    public static Gson create() {
        return new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(OffsetDateTime.class, new OffsetDateTimeAdapter())
                .create();
    }
}
