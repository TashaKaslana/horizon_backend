package org.phong.horizon.core.utils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.phong.horizon.core.annotations.ExcludeFromPayload;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public final class ObjectConversion {

    private static final ObjectMapper mapper;
    private static final Map<Class<?>, Set<String>> excludedFieldsCache = new ConcurrentHashMap<>();

    static {
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.addMixIn(org.springframework.context.ApplicationEvent.class, IgnoreSourceMixin.class);
    }

    private ObjectConversion() {}

    public static Map<String, Object> convertObjectToMap(Object obj) {
        return mapper.convertValue(obj, new TypeReference<Map<String, Object>>() {});
    }

    public static Map<String, Object> convertObjectToFilteredMap(Object obj) {
        return convertObjectToFilteredMap(obj, null);
    }

    public static Map<String, Object> convertObjectToFilteredMap(Object obj, Set<String> includedFields) {
        Map<String, Object> fullMap = convertObjectToMap(obj);
        Set<String> excludedFields = getExcludedFieldNames(obj.getClass());

        return fullMap.entrySet().stream()
                .filter(entry -> !excludedFields.contains(entry.getKey()))
                .filter(entry -> includedFields == null || includedFields.contains(entry.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public static Map<String, Object> convertObjectToPartialMap(Object obj, String... includedFields) {
        return convertObjectToFilteredMap(obj, Set.of(includedFields));
    }

    private static Set<String> getExcludedFieldNames(Class<?> clazz) {
        return excludedFieldsCache.computeIfAbsent(clazz, ObjectConversion::findExcludedFields);
    }

    private static Set<String> findExcludedFields(Class<?> clazz) {
        Set<String> result = new HashSet<>();
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(ExcludeFromPayload.class)) {
                result.add(field.getName());
            }
        }
        return result;
    }

    public abstract static class IgnoreSourceMixin {
        @JsonIgnore
        abstract Object getSource();
    }
}
